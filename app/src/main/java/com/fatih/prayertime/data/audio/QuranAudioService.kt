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
import com.fatih.prayertime.util.model.enums.PlaybackMode
import com.fatih.prayertime.util.model.state.AudioPlayerState
import com.fatih.prayertime.util.model.state.Status
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException
import java.net.SocketTimeoutException
import javax.inject.Inject
import javax.inject.Singleton

@AndroidEntryPoint
class QuranAudioService() : Service() {

    @Inject
    lateinit var audioStateManager: AudioStateManager

    private var mediaPlayer: MediaPlayer? = null
    private val audioState : AudioPlayerState by lazy {
        audioStateManager.audioState
    }

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)


    @Inject
    lateinit var getAudioFileUseCase: GetAudioFileUseCase

    private val notificationManager by lazy { getSystemService(NOTIFICATION_SERVICE) as NotificationManager }
    private val binder = LocalBinder()

    companion object {

        private const val NOTIFICATION_CHANNEL_ID = "quran_audio_channel"
        private const val NOTIFICATION_ID = 1
        private const val ACTION_PLAY = "com.fatih.prayertime.PLAY"
        private const val ACTION_PAUSE = "com.fatih.prayertime.PAUSE"
        private const val ACTION_NEXT = "com.fatih.prayertime.NEXT"
        private const val ACTION_PREVIOUS = "com.fatih.prayertime.PREVIOUS"
        private const val ACTION_STOP = "com.fatih.prayertime.STOP"
    }

    inner class LocalBinder : Binder() {
        fun getService(): QuranAudioService = this@QuranAudioService
    }

    override fun onCreate() {

        super.onCreate()
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

    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (audioState.currentAudioInfo == null) return
            when (intent?.action) {
                ACTION_PLAY -> {
                    if (mediaPlayer?.isPlaying == true) {
                        pauseAudio()
                    } else {
                        resumeAudio()
                    }
                }

                ACTION_NEXT -> {
                    CoroutineScope(Dispatchers.IO).launch {
                        getNextAudio()
                    }
                }

                ACTION_PREVIOUS -> {
                    CoroutineScope(Dispatchers.IO).launch {
                        getPreviousAudio()
                    }
                }

                ACTION_STOP -> {
                    stopAudio()
                }
            }
        }
    }

    private var audioDownloadJob : Job? = null

    fun downloadAndPlayAudioFile() {
        println("download")
        if (audioState.currentAudioInfo == null) return
        val (_, currentAudioNumber, reciter, _, bitrate, playbackMode, _, shouldCacheAudio) = audioState.currentAudioInfo!!

        val audioPath = if (playbackMode == PlaybackMode.SURAH) "audio-surah" else "audio"
        audioDownloadJob?.cancel()
        audioDownloadJob = serviceScope.launch {

            var currentRetry = 0
            var lastError: Exception? = null
            while (currentRetry < 3){
                if (!isActive) return@launch
                try {
                    getAudioFileUseCase.invoke(
                        audioPath, bitrate, reciter, currentAudioNumber, shouldCacheAudio
                    ).collect { resource ->
                        when (resource.status) {
                            Status.SUCCESS -> {
                                audioStateManager.updateState {
                                    copy(
                                        isLoading = false,
                                        error = null,
                                        downloadProgress = 100,
                                        downloadedSize = resource.totalSize,
                                        totalSize = resource.totalSize
                                    )
                                }
                                resource.data?.let { file ->
                                    playAudio(file)
                                }
                                break
                            }

                            Status.ERROR -> {
                                throw Exception(resource.message)
                            }

                            Status.LOADING -> {
                                audioStateManager.updateState {
                                    copy(
                                        duration = 0f,
                                        currentPosition = 0f,
                                        isPlaying = false,
                                        isLoading = true,
                                        error = null,
                                        downloadProgress = resource.progress,
                                        downloadedSize = resource.downloadedSize,
                                        totalSize = resource.totalSize
                                    )
                                }
                            }
                        }
                    }
                } catch (e: Exception) {
                    lastError = e
                    currentRetry++
                    if (currentRetry < 3) {
                        delay(1000L * (1 shl currentRetry))
                        continue
                    }
                    audioStateManager.updateState {
                        copy(
                            error = when (e) {
                                is SocketTimeoutException -> "Bağlantı zaman aşımına uğradı. Lütfen tekrar deneyin."
                                is IOException -> "İnternet bağlantınızı kontrol edin."
                                else -> e.message ?: "Ses dosyası oynatılamadı"
                            },
                            isLoading = false,
                            isPlaying = false,
                            downloadProgress = 0,
                            downloadedSize = 0,
                            totalSize = 0
                        )
                    }
                }
            }
        }
    }

    private fun getNextAudio() {
        val currentAudioInfo = audioState.currentAudioInfo
        audioStateManager.updateState {
            copy(
                currentAudioInfo = currentAudioInfo!!.copy(
                    audioNumber = when(currentAudioInfo.playbackMode){
                        PlaybackMode.VERSE_STREAM -> {
                            (currentAudioInfo.audioNumber + 1).coerceAtMost(6236)
                        }
                        PlaybackMode.SURAH -> {
                            (currentAudioInfo.audioNumber + 1).coerceAtMost(114)
                        }
                    }
                )
            )
        }
        downloadAndPlayAudioFile()
    }

    private fun getPreviousAudio() {
        val currentAudioInfo = audioState.currentAudioInfo
        audioStateManager.updateState {
            copy(
                currentAudioInfo = currentAudioInfo!!.copy(
                    audioNumber = (currentAudioInfo.audioNumber - 1).coerceAtLeast(1)
                )
            )
        }
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
                        .build()
                )

                setDataSource(file.path)
                prepare()

                setOnCompletionListener {
                    audioStateManager.updateState { copy(isPlaying = false) }
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

                start()
            }

            audioStateManager.updateState {
                copy(
                    isPlaying = true,
                    error = null
                )
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
        updateNotification(false)
    }

    fun resumeAudio() {
        mediaPlayer?.start()
        audioStateManager.updateState { copy(isPlaying = true) }
        startProgressTracking()
        updateNotification(true)
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
        mediaPlayer?.seekTo((position * 1000).toInt())
        audioStateManager.updateState { copy(currentPosition = position) }
    }

    fun cancelAudioDownload(){
        audioDownloadJob?.cancel()
        audioDownloadJob = null
    }

    private var progressJob: Job? = null

    private fun startProgressTracking() {
        progressJob?.cancel()
        mediaPlayer?.let { player ->
            progressJob = CoroutineScope(Dispatchers.Default).launch {
                if (player.isPlaying && isActive) {
                    val progress = player.currentPosition.toFloat() / player.duration
                    val duration = player.duration.toFloat()
                    if (!progress.isNaN() && !duration.isNaN()) {
                        audioStateManager.updateState {
                            copy(currentPosition = progress, duration = duration)
                        }
                    }
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

    private fun createNotification(isPlaying: Boolean): Notification {
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

        val (surahName, currentAudioNumber, _, reciterName, _, _, _, _) = audioState.currentAudioInfo!!
        return NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.quran)
            .setContentTitle(
                "$surahName - ${
                    getString(
                        R.string.quran_audio_verse,
                        currentAudioNumber
                    )
                }"
            )
            .setContentText(reciterName)
            .setSubText(getString(R.string.quran_audio_notification_subtext))
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
            .setDeleteIntent(deleteIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setContentIntent(contentIntent)
            .setOnlyAlertOnce(true)
            .setAutoCancel(true)
            .build()
    }

    private fun updateNotification(isPlaying: Boolean) {
        notificationManager.notify(NOTIFICATION_ID, createNotification(isPlaying))
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
        audioDownloadJob?.cancel()
        audioDownloadJob = null
        unregisterReceiver(broadcastReceiver)
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        stopAudio()
    }
}