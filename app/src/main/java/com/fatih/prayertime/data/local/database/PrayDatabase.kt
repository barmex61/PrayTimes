package com.fatih.prayertime.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.fatih.prayertime.data.local.dao.PrayDao
import com.fatih.prayertime.domain.model.PrayTimes

@Database(entities = [PrayTimes::class], version = 1, exportSchema = false)
abstract class PrayDatabase : RoomDatabase() {
    abstract fun prayDao() : PrayDao
}