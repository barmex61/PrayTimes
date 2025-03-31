package com.fatih.prayertime.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.fatih.prayertime.domain.model.PrayerAlarm
import kotlinx.coroutines.flow.Flow

@Dao
interface PrayerAlarmDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGlobalAlarm(prayerAlarm: PrayerAlarm)

    @Update
    suspend fun updateGlobalAlarm(prayerAlarm: PrayerAlarm)

    @Query("SELECT * FROM PrayerAlarm WHERE alarmType = :alarmType")
    suspend fun getGlobalAlarmByType(alarmType: String): PrayerAlarm?

    @Query("SELECT * FROM PrayerAlarm")
    fun getAllGlobalAlarms(): Flow<List<PrayerAlarm>>
}