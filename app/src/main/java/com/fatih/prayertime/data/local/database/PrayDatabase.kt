package com.fatih.prayertime.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.fatih.prayertime.data.local.dao.PrayDao
import com.fatih.prayertime.domain.model.PrayTimes
import com.fatih.prayertime.util.PairConverter

@TypeConverters(PairConverter::class)
@Database(entities = [PrayTimes::class], version = 1, exportSchema = false)
abstract class PrayDatabase : RoomDatabase() {
    abstract fun prayDao() : PrayDao
}