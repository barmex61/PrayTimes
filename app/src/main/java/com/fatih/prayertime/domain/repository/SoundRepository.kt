package com.fatih.prayertime.domain.repository

import com.fatih.prayertime.domain.model.PlaybackState
import com.fatih.prayertime.domain.model.Sound
import kotlinx.coroutines.flow.Flow

interface SoundRepository {
    fun getSystemSounds(): List<Sound>
    fun playSound(uri: String): Flow<PlaybackState>
    fun stopSound()

} 