package com.fatih.prayertime.data.settings

import android.content.Context
import android.util.Log

import androidx.datastore.preferences.core.edit
import com.fatih.prayertime.domain.model.GlobalAlarm

import com.fatih.prayertime.domain.model.Settings
import com.fatih.prayertime.util.Constants.SETTINGS_KEY
import com.fatih.prayertime.util.dataStore
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
            println("not null")
            Json.decodeFromString<Settings>(jsonString)
        } else {
            println("null")
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