package com.fatih.prayertime

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.fatih.prayertime.data.settings.SettingsDataStore
import com.fatih.prayertime.domain.use_case.alarm_use_cases.ScheduleDailyAlarmUpdateUseCase
import com.fatih.prayertime.util.Constants
import com.fatih.prayertime.util.convertJsonToEsmaulHusnaList
import com.fatih.prayertime.util.getJsonFromAssets
import com.jakewharton.threetenabp.AndroidThreeTen
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class MyApplication : Application() ,Configuration.Provider{

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override fun onCreate() {
        AndroidThreeTen.init(this)
        loadEsmaulHusnaList()
        deleteDatabase("global_alarm")
        deleteDatabase("pray_database ")
        super.onCreate()
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .setMinimumLoggingLevel(android.util.Log.DEBUG)
            .build()

    private fun loadEsmaulHusnaList() {
        val jsonString = getJsonFromAssets("esmaulHusna.json",this)
        Constants.esmaulHusnaList = convertJsonToEsmaulHusnaList(jsonString)
    }
}