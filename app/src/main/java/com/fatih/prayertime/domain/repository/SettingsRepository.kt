package com.fatih.prayertime.domain.repository

import com.fatih.prayertime.domain.model.Settings
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    suspend fun saveSettings(settings: Settings)
    fun getSettings() : Flow<Settings>
}