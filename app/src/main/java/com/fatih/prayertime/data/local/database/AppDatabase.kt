package com.fatih.prayertime.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.fatih.prayertime.data.local.dao.FavoritesDao
import com.fatih.prayertime.data.local.dao.PrayerStatisticsDao
import com.fatih.prayertime.data.local.entity.FavoritesEntity
import com.fatih.prayertime.data.local.entity.PrayerStatisticsEntity

@Database(
    entities = [
        FavoritesEntity::class,
        PrayerStatisticsEntity::class
    ],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun favoritesDao(): FavoritesDao
    abstract fun prayerStatisticsDao(): PrayerStatisticsDao

    companion object {
        const val DATABASE_NAME = "prayer_app_db"
    }
} 