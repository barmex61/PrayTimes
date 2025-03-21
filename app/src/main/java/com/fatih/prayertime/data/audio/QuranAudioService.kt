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
import com.fatih.prayertime.util.config.ApiConfig
import com.fatih.prayertime.util.model.enums.PlaybackMode
import com.fatih.prayertime.util.model.state.Status
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@AndroidEntryPoint
class QuranAudioService : Service() {

    private var mediaPlayer: MediaPlayer? = null
    private var progressCallback: ((Float, Float) -> Unit)? = null
    private var errorCallback: ((String) -> Unit)? = null
    private var isPlayingCallback: ((Boolean) -> Unit)? = null
    private var ayahChangeCallback : ((Int)-> Unit)? = null
    private var currentAudioNumber: Int = 0
    private var bitrate : Int = 192
    private var surahName : String = ""
    private var currentReciter: String = "ar.abdullahbasfar"
    private var shouldCacheAudio : Boolean = false
    private var playbackMode : PlaybackMode = PlaybackMode.VERSE_STREAM
    private var reciterName : String = ""
    private var speed: Float = 1.0f

    @Inject
    lateinit var getAudioFileUseCase: GetAudioFileUseCase


    private val notificationManager by lazy { getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager }
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
            },RECEIVER_NOT_EXPORTED)
        }else{
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
            when (intent?.action) {
                ACTION_PLAY -> {
                    if (mediaPlayer?.isPlaying == true) {
                        pauseAudio()
                    } else {
                        resumeAudio {}
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

    private suspend fun getAndPlayAudioFile(direction : Int){
        val audioPath : String
        when(playbackMode){
            PlaybackMode.SURAH -> {
                audioPath = "audio-surah"
                currentAudioNumber = (currentAudioNumber + direction).coerceIn(1,114)
            }
            PlaybackMode.VERSE_STREAM -> {
                audioPath = "audio"
                currentAudioNumber = (currentAudioNumber + direction).coerceIn(1,6236)
            }
        }
        try {
            getAudioFileUseCase.invoke(
                audioPath,bitrate,currentReciter,currentAudioNumber,shouldCacheAudio
            ).collect { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        resource.data?.let { file ->
                            playAudio(file)
                        }
                    }
                    Status.ERROR -> {
                        println(resource.message)
                        errorCallback?.invoke(resource.message ?: getString(R.string.quran_audio_error_download))
                    }
                    Status.LOADING -> {
                    }
                }
            }
        } catch (e: Exception) {
            println(e.message)
            errorCallback?.invoke(e.message ?: getString(R.string.quran_audio_error_generic))
        }
    }

    private suspend fun getNextAudio() {
        if (ayahChangeCallback != null) {
            ayahChangeCallback?.invoke(1)
            return
        }
        getAndPlayAudioFile(1)

    }

    private suspend fun getPreviousAudio() {
        if (currentAudioNumber > 1) {
            if (ayahChangeCallback != null) {
                ayahChangeCallback?.invoke(-1)
                return
            }
            getAndPlayAudioFile(-1)
        }
    }

    fun setCurrentAudioInfo(surahName : String, ayahNumber: Int, reciter: String, reciterName : String, shouldCacheAudio : Boolean, speed : Float,bitrate : Int,playbackMode: PlaybackMode) {
        this.surahName = surahName
        currentAudioNumber = ayahNumber
        currentReciter = reciter
        this.speed = speed
        this.shouldCacheAudio = shouldCacheAudio
        this.reciterName = reciterName
        this.bitrate = bitrate
        this.playbackMode = playbackMode
    }

    fun playAudio(audioFile: File) {
        try {

            if (mediaPlayer == null) {
                mediaPlayer = MediaPlayer().apply {
                    setAudioAttributes(
                        AudioAttributes.Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .build()
                    )
                    setOnCompletionListener {
                        CoroutineScope(Dispatchers.IO).launch {
                            getNextAudio()
                        }
                    }
                    setOnErrorListener { _, _, _ ->
                        errorCallback?.invoke(getString(R.string.quran_audio_error_playback))
                        false
                    }
                }
            }
            mediaPlayer?.apply {
                reset()
                setDataSource(audioFile.path)
                setOnPreparedListener {
                    it.start()
                    isPlayingCallback?.invoke(true)
                    startProgressTracking()
                    try {
                        it.playbackParams = it.playbackParams.setSpeed(speed) ?: return@setOnPreparedListener
                    } catch (e: Exception) {
                        println("Playback speed error: ${e.message}")
                    }
                    startForeground(NOTIFICATION_ID, createNotification(true))
                }
                
                prepare()
                if (duration > 0) {
                    progressCallback?.invoke(0f, duration.toFloat())
                }
            }
        } catch (e: Exception) {
            println("MediaPlayer error: ${e.message}")
            errorCallback?.invoke(e.message ?: getString(R.string.quran_audio_error_playback))
        }
    }

    fun pauseAudio() {
        mediaPlayer?.pause()
        isPlayingCallback?.invoke(false)
        stopProgressTracking()
        updateNotification(false)
    }

    fun resumeAudio(checkAudioFile: () -> Unit) {
        if (mediaPlayer == null) {
            checkAudioFile()
            return
        }
        mediaPlayer?.start()
        isPlayingCallback?.invoke(true)
        startProgressTracking()
        updateNotification(true)
    }

    fun stopAudio() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
        isPlayingCallback?.invoke(false)
        stopProgressTracking()
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    fun seekTo(progress: Float) {
        mediaPlayer?.let {
            val position = (progress * it.duration).toInt()
            it.seekTo(position)
        }
    }

    fun releaseMediaPlayer() {
        stopAudio()
    }

    private var progressTracker: Job? = null

    private fun startProgressTracking() {
        progressTracker?.cancel()
        progressTracker = CoroutineScope(Dispatchers.Main).launch {

            while (isActive) {
                try {
                    mediaPlayer?.let { player ->
                        if (player.isPlaying && !isReleased()) {
                            val progress = player.currentPosition.toFloat() / player.duration.toFloat()
                            val duration = player.duration.toFloat()
                            if (!duration.isNaN() && !progress.isNaN()){
                                progressCallback?.invoke(progress, duration)
                            }
                        }
                    }
                } catch (e: IllegalStateException) {
                    stopProgressTracking()
                    break
                } catch (e: Exception) {
                    stopProgressTracking()
                    break
                }
                delay(100)
            }
        }
    }

    private fun isReleased(): Boolean {
        return try {
            mediaPlayer?.duration
            false
        } catch (e: IllegalStateException) {
            true
        }
    }

    private fun stopProgressTracking() {
        progressTracker?.cancel()
        progressTracker = null
    }

    fun setProgressCallback(callback: ((Float, Float) -> Unit)?) {
        progressCallback = callback

    }


    fun setAyahChangedCallback(callback: ((Int) -> Unit)?) {
        ayahChangeCallback = callback
    }

    fun setErrorCallback(callback: ((String) -> Unit)?) {
        errorCallback = callback
    }

    fun setIsPlayingCallback(callback: ((Boolean) -> Unit)?) {
        isPlayingCallback = callback
        isPlayingCallback?.invoke(mediaPlayer?.isPlaying == true)
    }

    fun setPlaybackSpeed(newSpeed: Float) {
        speed = newSpeed
        try {
            mediaPlayer?.playbackParams = mediaPlayer?.playbackParams?.setSpeed(newSpeed) ?: return
        } catch (e: Exception) {

        }
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

        val contentIntent =  PendingIntent.getActivity(
            this,
            4,
            Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            },
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)


        return NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.quran)
            .setContentTitle("$surahName - ${getString(R.string.quran_audio_verse, currentAudioNumber)}")
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
        startForeground(NOTIFICATION_ID, createNotification(mediaPlayer?.isPlaying == true))
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mediaPlayer?.isPlaying == true) {
            mediaPlayer?.pause()
        }
        unregisterReceiver(broadcastReceiver)
        stopProgressTracking()
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        stopAudio()
    }

    private fun releaseResources() {
        try {
            mediaPlayer?.reset()
            mediaPlayer?.release()
            mediaPlayer = null
            mediaPlayer = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )
                setOnCompletionListener {
                    CoroutineScope(Dispatchers.IO).launch {
                        getNextAudio()
                    }
                }
                setOnErrorListener { _, _, _ ->
                    errorCallback?.invoke(getString(R.string.quran_audio_error_playback))
                    false
                }
            }
        } catch (e: Exception) {
            println("Error releasing resources: ${e.message}")
        }
    }

} 