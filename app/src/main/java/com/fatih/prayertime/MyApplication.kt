package com.fatih.prayertime

import android.app.Application
import com.jakewharton.threetenabp.AndroidThreeTen
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApplication : Application() {

    override fun onCreate() {
        AndroidThreeTen.init(this)
        super.onCreate()
    }
}