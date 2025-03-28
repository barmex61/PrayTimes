package com.fatih.prayertime.data.settings

import android.content.Context
import android.content.Context.MODE_PRIVATE
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import androidx.core.content.edit

@Singleton
class SharedPreferencesManager @Inject constructor(
    @ApplicationContext val context: Context,
) {

    companion object{
        val STATISTICS_KEY = "statistics"
    }

    val sharedPreferences = context.getSharedPreferences(context.packageName,MODE_PRIVATE)

    fun getStatisticKey() = sharedPreferences.getBoolean(STATISTICS_KEY,false)
    fun insertStatisticKey() = sharedPreferences.edit { putBoolean(STATISTICS_KEY, true) }
    
    fun clearStatisticKey() = sharedPreferences.edit { remove(STATISTICS_KEY) }

}