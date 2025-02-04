package com.fatih.prayertime.data.settings

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.fatih.prayertime.domain.model.Settings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings_prefs")
@Singleton
class SettingsDataStore @Inject constructor(private val context: Context) {

    private val SETTINGS_KEY = stringPreferencesKey("settings_json")
    val settings : Flow<Settings>
        get() = context.dataStore.data.map { prefs ->
        val jsonString = prefs[SETTINGS_KEY]
        if (jsonString != null) {
            Json.decodeFromString<Settings>(jsonString)
        } else {
            Settings()
        }
    }

    suspend fun updateSettings(settings: Settings) {
        val jsonString = Json.encodeToString(settings)
        context.dataStore.edit { prefs ->
            prefs[SETTINGS_KEY] = jsonString
        }
    }

}