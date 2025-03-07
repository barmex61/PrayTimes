package com.fatih.prayertime

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.fatih.prayertime.util.Constants
import com.fatih.prayertime.util.convertJsonToEsmaulHusnaList
import com.fatih.prayertime.util.getJsonFromAssets
import com.fatih.prayertime.util.loadDuaCategories
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
        loadPrayCategoryTr()
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

    private fun loadPrayCategoryTr(){
        Constants.prayCategoryTr = loadDuaCategories(this)
    }

}