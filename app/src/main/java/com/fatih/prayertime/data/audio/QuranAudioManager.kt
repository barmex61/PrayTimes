package com.fatih.prayertime.data.audio

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import com.fatih.prayertime.data.audio.QuranAudioService.LocalBinder
import java.io.File
import javax.inject.Inject

class QuranAudioManager @Inject constructor(
    private val context: Context
) {

    private var audioService: QuranAudioService? = null
    private var shouldRebind = false

    private var pendingProgressCallback: ((Float, Float) -> Unit)? = null
    private var pendingErrorCallback: ((String) -> Unit)? = null
    private var pendingIsPlayingCallback: ((Boolean) -> Unit)? = null
    private var ayahChangedCallback : ((Int) -> Unit)? = null

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as LocalBinder
            audioService = binder.getService()

            pendingProgressCallback?.let { callback ->
                audioService?.setProgressCallback(callback)
            }
            ayahChangedCallback?.let { callback->
                audioService?.setAyahChangedCallback(callback)
            }

            pendingErrorCallback?.let { callback ->
                audioService?.setErrorCallback(callback)
            }
            pendingIsPlayingCallback?.let { callback ->
                audioService?.setIsPlayingCallback(callback)
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            audioService = null
            if (shouldRebind) {
                bindService()
            }
        }
    }

    init {
        bindService()
    }

    private fun bindService() {
        Intent(context, QuranAudioService::class.java).also { intent ->
            context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
        }
    }

    fun setCurrentAudioInfo(surahName : String,audioNumber: Int, reciter: String,reciterName : String ,shouldCacheAudio: Boolean, speed : Float,bitrate: Int) {
        audioService?.setCurrentAudioInfo(surahName, audioNumber, reciter,reciterName, shouldCacheAudio,speed,bitrate)
    }

    fun playAudio(audioFile : File){
        audioService?.playAudio(audioFile)
    }

    fun pauseAudio(){
        audioService?.pauseAudio()
    }

    fun resumeAudio(checkAudioFile :() -> Unit){
        audioService?.resumeAudio(checkAudioFile)
    }

    fun stopAudio(){
        audioService?.stopAudio()
    }

    fun seekTo(progress : Float){
        audioService?.seekTo(progress)
    }

    fun setProgressCallback(callback: ((Float, Float) -> Unit)?) {
        pendingProgressCallback = callback
        audioService?.setProgressCallback(callback)
    }

    fun setAyahChangedCallback(callback: ((Int) -> Unit)?) {
        ayahChangedCallback = callback
        audioService?.setAyahChangedCallback(callback)
    }

    fun setErrorCallback(callback: ((String) -> Unit)?) {
        pendingErrorCallback = callback
        audioService?.setErrorCallback(callback)
    }

    fun setIsPlayingCallback(callback: ((Boolean) -> Unit)?) {
        pendingIsPlayingCallback = callback
        audioService?.setIsPlayingCallback(callback)
    }

    fun setPlaybackSpeed(speed : Float){
        audioService?.setPlaybackSpeed(speed)
    }

    fun releaseMediaPlayer() {
        audioService?.releaseMediaPlayer()
        context.unbindService(serviceConnection)
        shouldRebind = false
        pendingProgressCallback = null
        pendingErrorCallback = null
        pendingIsPlayingCallback = null
    }



}