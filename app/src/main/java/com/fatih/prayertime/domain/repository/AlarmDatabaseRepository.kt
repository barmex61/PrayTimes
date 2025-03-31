package com.fatih.prayertime.domain.repository

import com.fatih.prayertime.domain.model.PrayerAlarm
import com.fatih.prayertime.domain.model.PrayTimes
import kotlinx.coroutines.flow.Flow

interface AlarmDatabaseRepository {

    suspend fun insertPrayerAlarm(prayerAlarm: PrayerAlarm)
    suspend fun updatePrayerAlarm(prayerAlarm: PrayerAlarm)
    suspend fun getPrayerAlarmByType(alarmType : String): PrayerAlarm?
    fun getAllPrayerAlarms() : Flow<List<PrayerAlarm>>
    fun updateStatisticsAlarmForPrayTime(prayTimes: PrayTimes)
    fun updateStatisticsAlarmForPrayType(prayTime: Long,alarmDate: String,alarmType: String)
}