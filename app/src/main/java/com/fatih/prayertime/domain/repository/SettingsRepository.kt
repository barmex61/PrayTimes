package com.fatih.prayertime.domain.repository

import com.fatih.prayertime.domain.model.Settings
import com.fatih.prayertime.domain.model.QuranMediaSettings
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    suspend fun saveSettings(settings: Settings)
    fun getSettings() : Flow<Settings>
    suspend fun saveAudioSettings(quranMediaSettings: QuranMediaSettings)
    fun getAudioSettings(): Flow<QuranMediaSettings>

}