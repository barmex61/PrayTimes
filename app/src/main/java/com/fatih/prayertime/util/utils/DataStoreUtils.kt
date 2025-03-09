package com.fatih.prayertime.util.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore

object DataStoreUtils {
    val SETTINGS_KEY = stringPreferencesKey("settings_json")
    val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings_prefs")
} 