package com.fatih.prayertime.data.audio

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.fatih.prayertime.R
import com.fatih.prayertime.domain.use_case.quran_use_cases.GetAudioFileUseCase
import com.fatih.prayertime.presentation.main_activity.MainActivity
import com.fatih.prayertime.util.extensions.getExactAudioInfo
import com.fatih.prayertime.util.extensions.getNextAudioInfo
import com.fatih.prayertime.util.extensions.getPreviousAudioInfo
import com.fatih.prayertime.util.model.enums.PlaybackMode
import com.fatih.prayertime.util.model.state.AudioPlayerState
import com.fatih.prayertime.util.model.state.DownloadRequest
import com.fatih.prayertime.util.model.state.Resource
import com.fatih.prayertime.util.model.state.Status
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException
import java.net.SocketTimeoutException
import javax.inject.Inject
import kotlin.compareTo
import kotlin.coroutines.cancellation.CancellationException
import kotlin.times

@AndroidEntryPoint
class QuranAudioService() : Service() {

    @Inject
    lateinit var audioStateManager: AudioStateManager

    private var mediaPlayer: MediaPlayer? = null
    private val audioState : StateFlow<AudioPlayerState> by lazy {
        audioStateManager.audioPlayerState
    }

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var currentDownloadJob: Job? = null
    private val downloadRequests = MutableStateFlow<DownloadRequest?>(null)

    @Inject
    lateinit var getAudioFileUseCase: GetAudioFileUseCase

    private val notificationManager by lazy { getSystemService(NOTIFICATION_SERVICE) as NotificationManager }
    private val binder = LocalBinder()


    companion object {

        private const val NOTIFICATION_CHANNEL_ID = "quran_audio_channel"
        private const val NOTIFICATION_ID = 11
        private const val ACTION_PLAY = "PLAY"
        private const val ACTION_PAUSE = "PAUSE"
        private const val ACTION_NEXT = "NEXT"
        private const val ACTION_PREVIOUS = "PREVIOUS"
        private const val ACTION_STOP = "STOP"
    }

    inner class LocalBinder : Binder() {
        fun getService(): QuranAudioService = this@QuranAudioService
    }

    override fun onCreate() {
        super.onCreate()
        startForeground(NOTIFICATION_ID,createNotification())
        setupDownloadFlow()
        setupAudioStateObserver()
        createNotificationChannel()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(broadcastReceiver, IntentFilter().apply {
                addAction(ACTION_PLAY)
                addAction(ACTION_PAUSE)
                addAction(ACTION_NEXT)
                addAction(ACTION_PREVIOUS)
                addAction(ACTION_STOP)
            }, RECEIVER_NOT_EXPORTED)
        } else {
            @SuppressLint("UnspecifiedRegisterReceiverFlag")
            registerReceiver(broadcastReceiver, IntentFilter().apply {
                addAction(ACTION_PLAY)
                addAction(ACTION_PAUSE)
                addAction(ACTION_NEXT)
                addAction(ACTION_PREVIOUS)
                addAction(ACTION_STOP)
            })
        }
    }

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    private fun setupDownloadFlow() {
        serviceScope.launch {
            try {
                downloadRequests
                    .filterNotNull()
                    .onEach { request ->
                        currentDownloadJob?.cancel()
                        stopAudio()
                        resetStateForNewDownload()
                    }
                    .flatMapLatest { request ->
                        getAudioFileUseCase.invoke(
                            request.audioPath,
                            request.bitrate,
                            request.reciter,
                            request.audioNumber,
                            request.shouldCache
                        )
                    }.catch { exception ->
                        println(exception)
                        handleException(exception)
                    }.collect { resource ->
                        handleResource(resource)
                    }
            } catch (e: Exception) {
                println(e)
            }
        }.invokeOnCompletion {
            println("completed")
        }
    }

    @OptIn(FlowPreview::class)
    private fun setupAudioStateObserver() {
        serviceScope.launch {
            audioState.collect { state ->
                updateNotification()
            }
        }
    }

    private fun resetStateForNewDownload() {
        audioStateManager.updateState {
            copy(
                isLoading = true,
                downloadProgress = 0,
                downloadedSize = 0,
                totalSize = 0,
                error = null
            )
        }
    }

    private fun handleResource(resource: Resource<File>) {
        when (resource.status) {
            Status.SUCCESS -> {
                resource.data?.let { file ->
                    playAudio(file)
                }
                handleSuccess()
            }
            Status.ERROR -> {
                handleError(resource)
            }
            Status.LOADING -> {
                handleLoading(resource)
            }
        }
    }

    private fun handleSuccess(){
        audioStateManager.updateState {
            copy(
                isPlaying = true,
                isLoading = false,
                error = null
            )
        }
    }

    private fun handleLoading(resource: Resource<File>) {

        audioStateManager.updateState {
            copy(
                isLoading = true,
                downloadProgress = resource.progress,
                downloadedSize = resource.downloadedSize,
                totalSize = resource.totalSize,
                error = null
            )
        }
    }

    private fun handleError(resource: Resource<File>) {
        updateErrorState(resource.message ?: getString(R.string.quran_audio_error_download))
    }

    private fun handleException(throwable: Throwable) {
        val errorMessage = when (throwable) {
            is CancellationException -> {
                return
            }
            is SocketTimeoutException -> {
                getString(R.string.quran_audio_error_timeout)
            }
            is IOException -> {
                getString(R.string.quran_audio_error_network)
            }
            is IllegalStateException -> {
                getString(R.string.quran_audio_error_state)
            }
            else -> {
                throwable.message ?: getString(R.string.quran_audio_error_generic)
            }
        }

        updateErrorState(errorMessage)
    }

    private fun updateErrorState(errorMessage: String) {
        audioStateManager.updateState {
            copy(
                duration = 0f,
                currentPosition = 0f,
                isPlaying = false,
                isLoading = false,
                error = errorMessage,
                downloadProgress = 0,
                downloadedSize = 0,
                totalSize = 0
            )
        }
    }

    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                ACTION_PLAY -> {
                    if (mediaPlayer?.isPlaying == true) {
                        pauseAudio()
                    } else {
                        resumeAudio()
                    }
                }

                ACTION_NEXT -> {
                    getNextAudio()
                }

                ACTION_PREVIOUS -> {
                    getPreviousAudio()
                }

                ACTION_STOP -> {
                    stopAudio()
                }
            }
        }
    }


    fun downloadAndPlayAudioFile() {
        val currentState = audioStateManager.audioPlayerState.value

        val (_, reciter, _, bitrate, _,audioPath, _, shouldCacheAudio, _) = currentState.currentAudioInfo
        val audioNumber = currentState.currentAudioInfo.audioNumber

        val downloadRequest = DownloadRequest(
            audioPath = audioPath,
            bitrate = bitrate,
            reciter = reciter,
            audioNumber = audioNumber,
            shouldCache = shouldCacheAudio
        )
        println(downloadRequest)
        downloadRequests.value = downloadRequest

    }

    fun getNextAudio() {
        val currentAudioInfo = audioState.value.currentAudioInfo
        val updatedAudioInfo = currentAudioInfo.getNextAudioInfo()
        if (updatedAudioInfo.surahNumber != currentAudioInfo.surahNumber){
            audioStateManager.updateState { copy(currentAudioInfo = updatedAudioInfo) }
            return
        }
        audioStateManager.updateState { copy(currentAudioInfo = updatedAudioInfo) }
        downloadAndPlayAudioFile()
    }

    fun getPreviousAudio() {
        val currentAudioInfo = audioState.value.currentAudioInfo
        val updatedAudioInfo = currentAudioInfo.getPreviousAudioInfo()
        if (updatedAudioInfo.surahNumber != currentAudioInfo.surahNumber){
            audioStateManager.updateState { copy(currentAudioInfo = updatedAudioInfo) }
            return
        }
        audioStateManager.updateState { copy(currentAudioInfo = updatedAudioInfo) }
        downloadAndPlayAudioFile()
    }

    fun getExactAudio(audioNumber : Int){
        val currentAudioInfo = audioState.value.currentAudioInfo
        val updatedAudioInfo = currentAudioInfo.getExactAudioInfo(audioNumber)
        audioStateManager.updateState { copy(currentAudioInfo = updatedAudioInfo) }
        downloadAndPlayAudioFile()
    }


    fun playAudio(file: File) {
        try {
            stopAndReleaseMediaPlayer()

            mediaPlayer = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setFlags(AudioAttributes.FLAG_AUDIBILITY_ENFORCED)
                        .build()
                )

                setDataSource(file.path)

                prepare()
                setOnCompletionListener {
                    audioStateManager.updateState { copy(isPlaying = false) }
                    getNextAudio()
                }

                setOnErrorListener { _, _, _ ->
                    audioStateManager.updateState {
                        copy(
                            error = "Ses dosyası oynatılırken bir hata oluştu",
                            isPlaying = false
                        )
                    }
                    true
                }
                playbackParams = playbackParams.setSpeed(audioState.value.currentAudioInfo.playbackSpeed)
                start()
            }
            startProgressTracking()
        } catch (e: Exception) {
            audioStateManager.updateState {
                copy(
                    error = "Ses dosyası oynatılamadı: ${e.message}",
                    isPlaying = false
                )
            }
        }
    }

    fun pauseAudio() {
        mediaPlayer?.pause()
        audioStateManager.updateState { copy(isPlaying = false) }
        stopProgressTracking()
        updateNotification()
    }

    fun resumeAudio() {
        if (mediaPlayer == null){
            downloadAndPlayAudioFile()
        }else{
            mediaPlayer?.start()
            audioStateManager.updateState { copy(isPlaying = true) }
            startProgressTracking()
        }
        updateNotification()
    }

    fun stopAudio() {
        stopAndReleaseMediaPlayer()
        audioStateManager.updateState {
            copy(
                isPlaying = false,
                currentPosition = 0f,
                duration = 0f
            )
        }
    }

    fun seekTo(position: Float) {
        if (mediaPlayer == null || audioState.value.isLoading) return
        try {
            if (mediaPlayer!!.isPlaying || mediaPlayer!!.currentPosition >= 0) {
                val mSec = (mediaPlayer!!.duration * position).coerceAtLeast(0f)
                mediaPlayer?.seekTo(mSec.toInt())
            }
        } catch (e: Exception) {
        }
    }

    fun setPlaybackSpeed(speed : Float){
        if (mediaPlayer == null) return
        mediaPlayer!!.playbackParams = mediaPlayer!!.playbackParams.setSpeed(speed)
    }

    fun cancelAudioDownload() {
        audioStateManager.updateState {
            copy(
                isLoading = false,
                downloadProgress = 0,
                downloadedSize = 0,
                totalSize = 0,
                error = null
            )
        }
    }

    private var progressJob: Job? = null

    private fun startProgressTracking() {
        progressJob?.cancel()
        mediaPlayer?.let { player ->
            progressJob = CoroutineScope(Dispatchers.Default).launch {
                try {
                    while (isActive && player.isPlaying) {
                        val progress = player.currentPosition.toFloat() / player.duration
                        val duration = player.duration.toFloat()
                        if (!progress.isNaN() && !duration.isNaN()) {
                            audioStateManager.updateState {
                                copy(currentPosition = progress, duration = duration)
                            }
                        }
                        delay(50)
                    }
                }catch (e: Exception){
                    println(e)
                }

            }
        }
    }

    private fun stopProgressTracking(position: Float? = null, duration: Float? = null) {
        if (position != null && duration != null) {
            audioStateManager.updateState {
                copy(
                    duration = duration,
                    currentPosition = position
                )
            }
        }
        progressJob?.cancel()
    }

    private fun stopAndReleaseMediaPlayer() {
        stopProgressTracking(0f, 0f)
        mediaPlayer?.apply {
            if (isPlaying) {
                stop()
            }
            release()
        }
        mediaPlayer = null
    }


    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            getString(R.string.quran_audio_channel_name),
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = getString(R.string.quran_audio_channel_description)
            setSound(null, null)
        }
        notificationManager.createNotificationChannel(channel)
    }

    private fun createNotification(): Notification {
        val playPauseIntent = PendingIntent.getBroadcast(
            this,
            0,
            Intent(ACTION_PLAY).setPackage(packageName),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val nextIntent = PendingIntent.getBroadcast(
            this,
            1,
            Intent(ACTION_NEXT).setPackage(packageName),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val previousIntent = PendingIntent.getBroadcast(
            this,
            2,
            Intent(ACTION_PREVIOUS).setPackage(packageName),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val deleteIntent = PendingIntent.getBroadcast(
            this,
            3,
            Intent(ACTION_STOP).setPackage(packageName),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val contentIntent = PendingIntent.getActivity(
            this,
            4,
            Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            },
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val (surahName, _, reciterName, _, playbackMode , _, _, _, _, _) = audioStateManager.audioState.currentAudioInfo
        val currentAudioNumber = audioState.value.currentAudioInfo.audioNumber
        val isLoading = audioStateManager.audioState.isLoading
        val isPlaying = audioState.value.isPlaying
        val downloadProgress = audioState.value.downloadProgress
        val title = when(playbackMode){
            PlaybackMode.SURAH -> {
                surahName
            }
            PlaybackMode.VERSE_STREAM -> {
                "$surahName - ${
                    getString(
                        R.string.quran_audio_verse,
                        currentAudioNumber
                    )
                }"
            }
        }
        val notificationBuilder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(if (isLoading) R.drawable.loading_animation else R.drawable.quran)
            .setContentTitle(title)
            .setContentText(if (isLoading) "İndiriliyor... %$downloadProgress" else reciterName)
            .setSubText(getString(R.string.quran_audio_notification_subtext))
            .setDeleteIntent(deleteIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setContentIntent(contentIntent)
            .setOnlyAlertOnce(true)
            .setAutoCancel(true)

        if (isLoading) {
            notificationBuilder.setProgress(100, downloadProgress, false)
        }

        if (!isLoading) {
            notificationBuilder
                .addAction(
                    R.drawable.previous,
                    getString(R.string.quran_audio_previous),
                    previousIntent
                )
                .addAction(
                    if (isPlaying) R.drawable.pause else R.drawable.play,
                    if (isPlaying) getString(R.string.quran_audio_pause) else getString(R.string.quran_audio_play),
                    playPauseIntent
                )
                .addAction(
                    R.drawable.next,
                    getString(R.string.quran_audio_next),
                    nextIntent
                )
                .setStyle(
                    androidx.media.app.NotificationCompat.MediaStyle()
                        .setShowActionsInCompactView(0, 1, 2)
                )
        }

        return notificationBuilder.build()
    }

    private fun updateNotification() {
        try {
            notificationManager.notify(NOTIFICATION_ID, createNotification())
        }catch (e: Exception){
            println(e)
        }
    }

    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        stopAudio()
        serviceScope.cancel()
        progressJob?.cancel()
        progressJob = null
        unregisterReceiver(broadcastReceiver)
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        println("taskRemoved")
        stopAudio()
    }

}