package com.fatih.prayertime.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.fatih.prayertime.data.local.dao.GlobalAlarmDao
import com.fatih.prayertime.data.local.entity.GlobalAlarm
import com.fatih.prayertime.domain.model.PrayTimes

@Database(entities = [GlobalAlarm::class], version = 1, exportSchema = false)
abstract class GlobalAlarmDatabase : RoomDatabase() {
    abstract fun globalAlarmDao() : GlobalAlarmDao
}