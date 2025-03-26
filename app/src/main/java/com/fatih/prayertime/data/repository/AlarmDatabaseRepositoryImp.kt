package com.fatih.prayertime.data.repository

import com.fatih.prayertime.data.local.dao.GlobalAlarmDao
import com.fatih.prayertime.domain.model.PrayerAlarm
import com.fatih.prayertime.domain.repository.AlarmDatabaseRepository
import com.fatih.prayertime.data.alarm.AlarmScheduler
import com.fatih.prayertime.domain.model.PrayTimes
import com.fatih.prayertime.util.model.enums.PrayTimesString
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class AlarmDatabaseRepositoryImp @Inject constructor(
    private val globalAlarmDao: GlobalAlarmDao,
    private val alarmScheduler: AlarmScheduler
): AlarmDatabaseRepository {

    override suspend fun insertGlobalAlarm(prayerAlarm: PrayerAlarm) {
        globalAlarmDao.insertGlobalAlarm(prayerAlarm)
    }

    override suspend fun updateGlobalAlarm(prayerAlarm: PrayerAlarm) {
        globalAlarmDao.updateGlobalAlarm(prayerAlarm)
        alarmScheduler.updatePrayAlarm(prayerAlarm)
    }

    override suspend fun getGlobalAlarmByType(alarmType: String): PrayerAlarm? {
        return globalAlarmDao.getGlobalAlarmByType(alarmType)
    }

    override fun getAllGlobalAlarms(): Flow<List<PrayerAlarm>> = flow{
        val globalAlarms = globalAlarmDao.getAllGlobalAlarms()
        if (globalAlarms.first().isEmpty()) {
            val initialAlarms = PrayTimesString.entries.filterIndexed { index, _ ->
                index <= 4
            }.map {
                PrayerAlarm(
                    alarmType = it.name,
                    alarmTime = 0L,
                    alarmTimeString = "16-01-2025 00:00",
                    isEnabled = false,
                    alarmOffset = 0
                )
            }
            initialAlarms.forEach { alarm->
                insertGlobalAlarmUseCase(alarm)
            }
        }
    }

    override fun updateStatisticsAlarmForPrayTime(prayTimes: PrayTimes) {
        alarmScheduler.updateStatisticsAlarmForPrayTime(prayTimes)
    }

    override  fun updateStatisticsAlarmForPrayType(
        prayTime: Long,
        alarmDate: String,
        alarmType: String
    ) {
        alarmScheduler.updateStatisticsAlarmForPrayType(prayTime,alarmDate,alarmType)
    }
}