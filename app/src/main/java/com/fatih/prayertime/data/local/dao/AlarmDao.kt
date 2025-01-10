package com.fatih.prayertime.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.fatih.prayertime.data.local.entity.AlarmTimes

@Dao
interface AlarmDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlarmTimes(alarmTimes: AlarmTimes)
    @Query("SELECT * FROM AlarmTimes WHERE id = :id")
    suspend fun getAlarmTimesById(id: Int = 0): AlarmTimes
}