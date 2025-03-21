package com.fatih.prayertime.data.settings

import android.content.Context

import androidx.datastore.preferences.core.edit

import com.fatih.prayertime.domain.model.Settings
import com.fatih.prayertime.domain.model.QuranMediaSettings
import com.fatih.prayertime.util.utils.DataStoreUtils.SETTINGS_KEY
import com.fatih.prayertime.util.utils.DataStoreUtils.AUDIO_SETTINGS_KEY
import com.fatih.prayertime.util.utils.DataStoreUtils.dataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject


class SettingsDataStore @Inject constructor(
    private val context: Context,
) {
    val settings : Flow<Settings>
        get() = context.dataStore.data.map { prefs ->
        val jsonString = prefs[SETTINGS_KEY]
        if (jsonString != null) {
            Json.decodeFromString<Settings>(jsonString)
        } else {
            Settings()
        }
    }

    val quranMediaSettings: Flow<QuranMediaSettings>
        get() = context.dataStore.data.map { prefs ->
            val jsonString = prefs[AUDIO_SETTINGS_KEY]
            if (jsonString != null) {
                Json.decodeFromString<QuranMediaSettings>(jsonString)
            } else {
                QuranMediaSettings()
            }
        }

    suspend fun updateSettings(settings: Settings) {
        val jsonString = Json.encodeToString(settings)
        context.dataStore.edit { prefs ->
            prefs[SETTINGS_KEY] = jsonString
        }
    }

    suspend fun updateAudioSettings(quranMediaSettings: QuranMediaSettings) {
        val jsonString = Json.encodeToString(quranMediaSettings)
        context.dataStore.edit { prefs ->
            prefs[AUDIO_SETTINGS_KEY] = jsonString
        }
    }

}