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
    private var completionCallback: (() -> Unit)? = null
    private var errorCallback: ((String) -> Unit)? = null
    private var isPlayingCallback: ((Boolean) -> Unit)? = null
    private var ayahChangeCallback : ((Int)-> Unit)? = null
    private var currentAyahNumber: Int = 0
    private var surahName : String = ""
    private var currentReciter: String = "ar.abdullahbasfar"
    private var currentQuality: String = "192"
    private var shouldCacheAudio : Boolean = false
    private var reciterName : String = ""
    private var speed: Float = 1.0f

    @Inject 
    lateinit var getAudioFileUseCase: GetAudioFileUseCase

    private val baseUrl = "https://cdn.islamic.network/quran/audio"
    private val notificationManager by lazy { getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager }
    private val binder = LocalBinder()

    companion object {
        private const val NOTIFICATION_CHANNEL_ID = "quran_audio_channel"
        private const val NOTIFICATION_ID = 1
        private const val ACTION_PLAY = "com.fatih.prayertime.PLAY"
        private const val ACTION_PAUSE = "com.fatih.prayertime.PAUSE"
        private const val ACTION_NEXT = "com.fatih.prayertime.NEXT"
        private const val ACTION_PREVIOUS = "com.fatih.prayertime.PREVIOUS"
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
            },RECEIVER_NOT_EXPORTED)
        }else{
            @SuppressLint("UnspecifiedRegisterReceiverFlag")
            registerReceiver(broadcastReceiver, IntentFilter().apply {
                addAction(ACTION_PLAY)
                addAction(ACTION_PAUSE)
                addAction(ACTION_NEXT)
                addAction(ACTION_PREVIOUS)
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
            }
        }
    }

    private suspend fun getNextAudio() {
        if (ayahChangeCallback != null) {
            ayahChangeCallback?.invoke(1)
            return
        }
        currentAyahNumber++
        val nextAudioUrl = "$baseUrl/$currentQuality/$currentReciter/$currentAyahNumber.mp3"
        try {
            getAudioFileUseCase(nextAudioUrl, shouldCacheAudio).collect { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        resource.data?.let { file ->
                            playAudio(file)
                        }
                    }
                    Status.ERROR -> {
                        errorCallback?.invoke(resource.message ?: "Ses dosyası yüklenemedi")
                    }
                    Status.LOADING -> {
                    }
                }
            }
        } catch (e: Exception) {
            println(e.message)
            errorCallback?.invoke(e.message ?: "Bir hata oluştu")
        }
    }

    private suspend fun getPreviousAudio() {
        if (currentAyahNumber > 1) {
            if (ayahChangeCallback != null) {
                ayahChangeCallback?.invoke(-1)
                return
            }
            currentAyahNumber--
            val previousAudioUrl = "$baseUrl/$currentQuality/$currentReciter/$currentAyahNumber.mp3"
            try {
                getAudioFileUseCase(previousAudioUrl, shouldCacheAudio).collect { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            resource.data?.let { file ->
                                playAudio(file)
                            }
                        }
                        Status.ERROR -> {
                            errorCallback?.invoke(resource.message ?: "Ses dosyası yüklenemedi")
                        }
                        Status.LOADING -> {
                        }
                    }
                }
            } catch (e: Exception) {
                errorCallback?.invoke(e.message ?: "Bir hata oluştu")
            }
        }
    }

    fun setCurrentAudioInfo(surahName : String, ayahNumber: Int, reciter: String, reciterName : String, shouldCacheAudio : Boolean, quality: String = "192") {
        this.surahName = surahName
        currentAyahNumber = ayahNumber
        currentReciter = reciter
        currentQuality = quality
        this.shouldCacheAudio = shouldCacheAudio
        this.reciterName = reciterName
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
                        errorCallback?.invoke("Ses dosyası oynatılamadı")
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
                    it.playbackParams = it.playbackParams.setSpeed(speed)
                    startForeground(NOTIFICATION_ID, createNotification(true))
                }
                prepareAsync()
            }
        } catch (e: Exception) {
            errorCallback?.invoke(e.message ?: "Ses dosyası oynatılamadı")
        }
    }

    fun pauseAudio() {
        try {
            if (mediaPlayer?.isPlaying == true) {
                mediaPlayer?.pause()
                isPlayingCallback?.invoke(false)
                stopProgressTracking()
                updateNotification(false)
            }
        } catch (e: Exception) {
            // Hata durumunda sessizce devam et
        }
    }

    fun resumeAudio(checkAudioFile: () -> Unit) {
        try {
            if (mediaPlayer == null) {
                checkAudioFile()
                return
            }
            mediaPlayer?.start()
            isPlayingCallback?.invoke(true)
            startProgressTracking()
            updateNotification(true)
        } catch (e: Exception) {
            checkAudioFile()
        }
    }

    fun stopAudio() {
        try {
            mediaPlayer?.stop()
            mediaPlayer?.release()
            mediaPlayer = null
            isPlayingCallback?.invoke(false)
            stopProgressTracking()
            stopForeground(STOP_FOREGROUND_REMOVE)
            stopSelf()
        } catch (e: Exception) {
            // Hata durumunda sessizce devam et
        }
    }

    fun seekTo(progress: Float) {
        try {
            mediaPlayer?.let {
                val position = (progress * it.duration).toInt()
                it.seekTo(position)
            }
        } catch (e: Exception) {
            // Hata durumunda sessizce devam et
        }
    }

    fun releaseMediaPlayer() {
        stopAudio()
        mediaPlayer?.release()
        mediaPlayer = null
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private var progressTracker: Job? = null

    private fun startProgressTracking() {
        progressTracker?.cancel()
        progressTracker = CoroutineScope(Dispatchers.Main).launch {
            while (isActive) {
                try {
                    mediaPlayer?.let { player ->
                        if (player.isPlaying) {
                            val progress = player.currentPosition.toFloat() / player.duration.toFloat()
                            val duration = player.duration.toFloat()
                            progressCallback?.invoke(progress, duration)
                        }
                    }
                } catch (e: Exception) {
                    // MediaPlayer geçersiz durumda olabilir, sessizce devam et
                }
                delay(100)
            }
        }
    }

    private fun stopProgressTracking() {
        progressTracker?.cancel()
        progressTracker = null
    }

    fun setProgressCallback(callback: ((Float, Float) -> Unit)?) {
        progressCallback = callback

    }

    fun setCompletionCallback(callback: (() -> Unit)?) {
        completionCallback = callback
    }

    fun setAyahChangedCallback(callback: ((Int) -> Unit)?) {
        ayahChangeCallback = callback
    }

    fun setErrorCallback(callback: ((String) -> Unit)?) {
        errorCallback = callback
    }

    fun setIsPlayingCallback(callback: ((Boolean) -> Unit)?) {
        isPlayingCallback = callback
    }

    fun setPlaybackSpeed(newSpeed: Float) {
        speed = newSpeed
        mediaPlayer?.playbackParams = mediaPlayer?.playbackParams?.setSpeed(newSpeed) ?: return
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            "Kuran Sesi",
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "Kuran ses kontrolleri"
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

        return NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.quran)
            .setContentTitle("$surahName - Ayet $currentAyahNumber")
            .setContentText(reciterName)
            .setSubText("Kuran-ı Kerim")
            .addAction(
                R.drawable.previous,
                "Önceki",
                previousIntent
            )
            .addAction(
                if (isPlaying) R.drawable.pause else R.drawable.play,
                if (isPlaying) "Duraklat" else "Oynat",
                playPauseIntent
            )
            .addAction(
                R.drawable.next,
                "Sonraki",
                nextIntent
            )
            .setStyle(
                androidx.media.app.NotificationCompat.MediaStyle()
                    .setShowActionsInCompactView(0, 1, 2)
            )
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setOnlyAlertOnce(true)
            .setOngoing(true)
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
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mediaPlayer?.isPlaying == true) {
            mediaPlayer?.pause()
        }
        unregisterReceiver(broadcastReceiver)
        stopProgressTracking()
    }

} 