package com.fatih.prayertime.data.audio

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import com.fatih.prayertime.data.audio.QuranAudioService.LocalBinder
import javax.inject.Inject

class QuranAudioManager @Inject constructor(
    private val context: Context
) {

    private var audioService: QuranAudioService? = null
    private var shouldRebind = false


    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as LocalBinder
            audioService = binder.getService()
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

    fun getExactAudio(audioNumber : Int){
        audioService?.getExactAudio(audioNumber)
    }


    fun playNext() {
        audioService?.getNextAudio()
    }

    fun playPrevious() {
        audioService?.getPreviousAudio()
    }

    fun pauseAudio() {
        audioService?.pauseAudio()
    }

    fun resumeAudio() {
        audioService?.resumeAudio()
    }

    fun stopAudio() {
        audioService?.stopAudio()
    }

    fun seekTo(position: Float) {
        audioService?.seekTo(position)
    }

    fun cancelAudioDownload() {
        audioService?.cancelAudioDownload()
    }

    fun setPlaybackSpeed(speed : Float){
        audioService?.setPlaybackSpeed(speed)
    }

    fun release() {
        shouldRebind = false
        context.unbindService(serviceConnection)
    }

}