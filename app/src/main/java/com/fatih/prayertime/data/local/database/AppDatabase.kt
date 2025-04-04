package com.fatih.prayertime.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.fatih.prayertime.data.local.dao.FavoritesDao
import com.fatih.prayertime.data.local.dao.PrayerAlarmDao
import com.fatih.prayertime.data.local.dao.PrayDao
import com.fatih.prayertime.data.local.dao.PrayerStatisticsDao
import com.fatih.prayertime.data.local.entity.FavoritesEntity
import com.fatih.prayertime.data.local.entity.PrayerStatisticsEntity
import com.fatih.prayertime.domain.model.PrayerAlarm
import com.fatih.prayertime.domain.model.PrayTimes

@Database(
    entities = [
        FavoritesEntity::class,
        PrayerStatisticsEntity::class,
        PrayerAlarm::class,
        PrayTimes::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun favoritesDao(): FavoritesDao
    abstract fun prayerStatisticsDao(): PrayerStatisticsDao
    abstract fun prayDao() : PrayDao
    abstract fun globalAlarmDao() : PrayerAlarmDao

    companion object {
        const val DATABASE_NAME = "prayer_app_db"
    }
} 