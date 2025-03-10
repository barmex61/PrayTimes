package com.fatih.prayertime.domain.repository

import com.fatih.prayertime.domain.model.PrayerAlarm
import com.fatih.prayertime.domain.model.PrayTimes
import kotlinx.coroutines.flow.Flow

interface AlarmDatabaseRepository {

    suspend fun insertGlobalAlarm(prayerAlarm: PrayerAlarm)
    suspend fun updateGlobalAlarm(prayerAlarm: PrayerAlarm)
    suspend fun getGlobalAlarmByType(alarmType : String): PrayerAlarm?
    fun getAllGlobalAlarms() : Flow<List<PrayerAlarm>>
    suspend fun updateStatisticsAlarm(prayTimes: PrayTimes)

}