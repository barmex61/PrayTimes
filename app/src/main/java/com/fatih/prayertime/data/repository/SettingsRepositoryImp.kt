package com.fatih.prayertime.data.repository

import com.exyte.animatednavbar.animation.indendshape.shapeCornerRadius
import com.fatih.prayertime.data.settings.SettingsDataStore
import com.fatih.prayertime.data.settings.SharedPreferencesManager
import com.fatih.prayertime.domain.model.Settings
import com.fatih.prayertime.domain.model.QuranMediaSettings
import com.fatih.prayertime.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SettingsRepositoryImp @Inject constructor(
    private val settingsDataStore: SettingsDataStore,
) : SettingsRepository {

    override suspend fun saveSettings(settings: Settings) {
         settingsDataStore.updateSettings(settings)
    }

    override fun getSettings(): Flow<Settings> {
        return settingsDataStore.settings
    }

    override suspend fun saveAudioSettings(quranMediaSettings: QuranMediaSettings) {
        settingsDataStore.updateAudioSettings(quranMediaSettings)
    }

    override fun getAudioSettings(): Flow<QuranMediaSettings> {
        return settingsDataStore.quranMediaSettings
    }

}