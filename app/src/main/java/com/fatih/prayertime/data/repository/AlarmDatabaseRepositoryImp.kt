package com.fatih.prayertime.data.repository

import com.fatih.prayertime.data.local.dao.PrayerAlarmDao
import com.fatih.prayertime.domain.model.PrayerAlarm
import com.fatih.prayertime.domain.repository.AlarmDatabaseRepository
import com.fatih.prayertime.data.alarm.AlarmScheduler
import com.fatih.prayertime.domain.model.PrayTimes
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AlarmDatabaseRepositoryImp @Inject constructor(
    private val prayerAlarmDao: PrayerAlarmDao,
    private val alarmScheduler: AlarmScheduler
): AlarmDatabaseRepository {

    override suspend fun insertPrayerAlarm(prayerAlarm: PrayerAlarm) {
        prayerAlarmDao.insertGlobalAlarm(prayerAlarm)
    }

    override suspend fun updatePrayerAlarm(prayerAlarm: PrayerAlarm) {
        prayerAlarmDao.updateGlobalAlarm(prayerAlarm)
        alarmScheduler.updatePrayAlarm(prayerAlarm)
    }

    override suspend fun getPrayerAlarmByType(alarmType: String): PrayerAlarm? {
        return prayerAlarmDao.getGlobalAlarmByType(alarmType)
    }

    override fun getAllPrayerAlarms(): Flow<List<PrayerAlarm>> {
        return prayerAlarmDao.getAllGlobalAlarms()
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