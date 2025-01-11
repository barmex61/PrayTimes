package com.fatih.prayertime.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.fatih.prayertime.data.local.entity.GlobalAlarm
import kotlinx.coroutines.flow.Flow

@Dao
interface GlobalAlarmDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGlobalAlarm(globalAlarm: GlobalAlarm)

    @Update
    suspend fun updateGlobalAlarm(globalAlarm: GlobalAlarm)

    @Query("SELECT * FROM GlobalAlarm WHERE alarmType = :alarmType")
    suspend fun getGlobalAlarmByType(alarmType: String): GlobalAlarm?

    @Query("SELECT * FROM GlobalAlarm")
    fun getAllGlobalAlarms(): Flow<List<GlobalAlarm>>
}