package com.fatih.prayertime.domain.repository

import com.fatih.prayertime.domain.model.Settings
import com.fatih.prayertime.domain.model.AudioSettings
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    suspend fun saveSettings(settings: Settings)
    fun getSettings() : Flow<Settings>
    suspend fun saveAudioSettings(audioSettings: AudioSettings)
    fun getAudioSettings(): Flow<AudioSettings>
}