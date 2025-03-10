package com.fatih.prayertime.data.repository

import com.fatih.prayertime.data.local.dao.GlobalAlarmDao
import com.fatih.prayertime.domain.model.PrayerAlarm
import com.fatih.prayertime.domain.repository.AlarmDatabaseRepository
import com.fatih.prayertime.data.alarm.AlarmScheduler
import com.fatih.prayertime.domain.model.PrayTimes
import kotlinx.coroutines.flow.Flow
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

    override fun getAllGlobalAlarms(): Flow<List<PrayerAlarm>> {
        return globalAlarmDao.getAllGlobalAlarms()
    }

    override suspend fun updateStatisticsAlarm(prayTimes: PrayTimes) {
        alarmScheduler.updateStatisticsAlarmForPrayTime(prayTimes)
    }
}