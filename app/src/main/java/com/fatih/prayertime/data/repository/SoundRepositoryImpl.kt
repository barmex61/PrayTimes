package com.fatih.prayertime.data.repository

import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.util.Log
import com.fatih.prayertime.R
import com.fatih.prayertime.domain.model.PlaybackState
import com.fatih.prayertime.domain.model.Sound
import com.fatih.prayertime.domain.repository.SoundRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.withContext
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton
import androidx.core.net.toUri

@Singleton
class SoundRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
) : SoundRepository {
    
    private var mediaPlayer: MediaPlayer? = null
    
    override fun getSystemSounds(): List<Sound> {
        return listOf(
            Sound(
                id = "notification_1",
                displayName = context.getString(R.string.sound_option_notification_1),
                uri = "android.resource://${context.packageName}/raw/notification_1"
            ),
            Sound(
                id = "notification_2",
                displayName = context.getString(R.string.sound_option_notification_2),
                uri = "android.resource://${context.packageName}/raw/notification_2"
            ),
            Sound(
                id = "sound_1",
                displayName = context.getString(R.string.sound_option_sound_1),
                uri = "android.resource://${context.packageName}/raw/sound_1"
            ),
            Sound(
                id = "sound_2",
                displayName = context.getString(R.string.sound_option_sound_2),
                uri = "android.resource://${context.packageName}/raw/sound_2"
            ),
            Sound(
                id = "sound_3",
                displayName = context.getString(R.string.sound_option_sound_3),
                uri = "android.resource://${context.packageName}/raw/sound_3"
            )
        )
    }
    

    
    override fun playSound(uri: String): Flow<PlaybackState> = callbackFlow {
        trySend(PlaybackState.Initial)
        
        stopSound()
        
        try {
            mediaPlayer = MediaPlayer().apply {
                when {
                    uri.startsWith("content://") || uri.startsWith("file://") -> {
                        setDataSource(context, uri.toUri())
                    }
                    uri.contains("android.resource") -> {
                        setDataSource(context, uri.toUri())
                    }
                    else -> {
                        // Raw folder resource
                        val resourceName = uri.substringAfterLast("/").substringBeforeLast(".")
                        val resId = context.resources.getIdentifier(resourceName, "raw", context.packageName)
                        val afd = context.resources.openRawResourceFd(resId)
                        setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
                        afd.close()
                    }
                }
                
                prepare()
                start()
                
                trySend(PlaybackState.Playing(uri))
                
                setOnCompletionListener {
                    trySend(PlaybackState.Stopped)
                    release()
                    mediaPlayer = null
                }
                
                setOnErrorListener { _, _, _ ->
                    trySend(PlaybackState.Error("Ses çalma hatası"))
                    release()
                    mediaPlayer = null
                    true
                }
            }
        } catch (e: IOException) {
            trySend(PlaybackState.Error(e.message ?: "Bilinmeyen hata"))
            mediaPlayer?.release()
            mediaPlayer = null
        }
        
        awaitClose {
            stopSound()
        }
    }
    
    override fun stopSound() {
        mediaPlayer?.apply {
            if (isPlaying) {
                stop()
            }
            release()
        }
        mediaPlayer = null
    }
} 