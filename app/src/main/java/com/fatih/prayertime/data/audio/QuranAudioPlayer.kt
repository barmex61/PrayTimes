package com.fatih.prayertime.data.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.os.Handler
import android.os.Looper
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QuranAudioPlayer @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private var mediaPlayer: MediaPlayer? = null
    private var currentAudioFile: File? = null
    private var progressJob: Job? = null

    private var progressCallback: ((Float, Float) -> Unit)? = null
    private var completionCallback: (() -> Unit)? = null
    private var errorCallback: ((String) -> Unit)? = null
    private var isPlayingCallback: ((Boolean) -> Unit)? = null
    private var speed : Float = 1f

    init {
        initializeMediaPlayer()
    }

    private fun initializeMediaPlayer() {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer().apply {
                setVolume(1f,1f)
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )
                
                setOnCompletionListener {
                    isPlayingCallback?.invoke(false)
                    completionCallback?.invoke()
                    stopProgressTracking()
                }
                
                setOnErrorListener { _, _, _ ->
                    isPlayingCallback?.invoke(false)
                    errorCallback?.invoke("Ses dosyası oynatılamadı")
                    stopProgressTracking()
                    true
                }
            }
        }
    }

    fun playAudio(audioFile: File) {
        initializeMediaPlayer()
        try {
            // Eğer aynı dosyayı çalıyorsak, baştan başlat
            if (currentAudioFile?.absolutePath == audioFile.absolutePath) {
                mediaPlayer?.seekTo(0)
                mediaPlayer?.start()
                isPlayingCallback?.invoke(true)
                startProgressTracking()
                return
            }

            // Farklı bir dosya çalınacaksa, mevcut çalanı durdur
            stopAudio()

            currentAudioFile = audioFile

            mediaPlayer?.apply {
                reset()
                setDataSource(context, Uri.fromFile(audioFile))
                setOnPreparedListener {
                    it.start()
                    isPlayingCallback?.invoke(true)
                    startProgressTracking()
                    it.playbackParams = it.playbackParams.setSpeed(speed)
                }
                prepareAsync()
            }
        } catch (e: Exception) {
            isPlayingCallback?.invoke(false)
            errorCallback?.invoke("Ses dosyası oynatılamadı: ${e.localizedMessage}")
        }
    }

    fun pauseAudio() {
        initializeMediaPlayer()
        mediaPlayer!!.pause()
        isPlayingCallback?.invoke(false)
        stopProgressTracking()
    }

    fun resumeAudio(checkAudioFile : () -> Unit) {
        initializeMediaPlayer()
        if (currentAudioFile == null) {
            checkAudioFile()
            return
        }
        mediaPlayer?.start()
        isPlayingCallback?.invoke(true)
        startProgressTracking()
    }

    fun stopAudio() {
        initializeMediaPlayer()
        mediaPlayer?.stop()
        isPlayingCallback?.invoke(false)
        progressCallback?.invoke(0f,0f)
        currentAudioFile = null
        stopProgressTracking()
    }

    fun seekTo(position: Float) {
        initializeMediaPlayer()
        mediaPlayer?.let { player ->
            val duration = player.duration
            val seekPosition = (duration * position).toInt()
            player.seekTo(seekPosition)
        }
    }

    fun setPlaybackSpeed(speed: Float) {
        this.speed = speed
        initializeMediaPlayer()
        mediaPlayer?.let {
            if (it.isPlaying || it.isLooping) {
                it.playbackParams = it.playbackParams.setSpeed(speed)
            }
        }
    }

    fun setProgressCallback(callback: (Float, Float) -> Unit) {
        progressCallback = callback
    }

    fun setCompletionCallback(callback: () -> Unit) {
        completionCallback = callback
    }

    fun setErrorCallback(callback: (String) -> Unit) {
        errorCallback = callback
    }

    fun setIsPlayingCallback(callback: (Boolean) -> Unit) {
        isPlayingCallback = callback
    }

    private fun startProgressTracking() {
        progressJob?.cancel()
        progressJob = CoroutineScope(Dispatchers.Default).launch {
            while (isActive && mediaPlayer?.isPlaying == true) {
                mediaPlayer?.let { player ->
                    if (player.duration > 0) {
                        val progress = player.currentPosition.toFloat() / player.duration.toFloat()
                        progressCallback?.invoke(progress,player.duration.toFloat())
                    }
                }
                delay(50)
            }
        }
    }

    private fun stopProgressTracking() {
        progressJob?.cancel()
        progressJob = null
    }

    fun releaseMediaPlayer() {
        stopProgressTracking()
        mediaPlayer?.release()
        mediaPlayer = null
        currentAudioFile = null
        isPlayingCallback?.invoke(false)
    }
} 