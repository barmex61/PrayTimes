package com.fatih.prayertime.domain.repository

import com.fatih.prayertime.data.local.entity.AlarmTimes

interface AlarmDatabaseRepository {

    suspend fun insertAlarmTimes(alarmTimes: AlarmTimes)
    suspend fun getAlarmTimesById(id: Int = 0): AlarmTimes

}