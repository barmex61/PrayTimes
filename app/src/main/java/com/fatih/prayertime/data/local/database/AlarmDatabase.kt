package com.fatih.prayertime.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.fatih.prayertime.data.local.dao.AlarmDao
import com.fatih.prayertime.data.local.entity.AlarmTimes
import com.fatih.prayertime.util.AlarmPairConverter

@TypeConverters(AlarmPairConverter::class)
@Database(entities = [AlarmTimes::class], version = 1, exportSchema = false)
abstract class AlarmDatabase : RoomDatabase() {
    abstract fun alarmDao() : AlarmDao
}