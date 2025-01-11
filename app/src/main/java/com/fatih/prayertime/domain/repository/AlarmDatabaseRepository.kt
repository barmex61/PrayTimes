package com.fatih.prayertime.domain.repository

import com.fatih.prayertime.domain.model.GlobalAlarm
import kotlinx.coroutines.flow.Flow

interface AlarmDatabaseRepository {

    suspend fun insertGlobalAlarm(globalAlarm: GlobalAlarm)
    suspend fun updateGlobalAlarm(globalAlarm: GlobalAlarm)
    suspend fun getGlobalAlarmByType(alarmType : String): GlobalAlarm?
    fun getAllGlobalAlarms() : Flow<List<GlobalAlarm>>

}