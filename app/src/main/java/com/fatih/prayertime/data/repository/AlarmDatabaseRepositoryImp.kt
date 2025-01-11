package com.fatih.prayertime.data.repository

import com.fatih.prayertime.data.local.dao.GlobalAlarmDao
import com.fatih.prayertime.data.local.entity.GlobalAlarm
import com.fatih.prayertime.domain.repository.AlarmDatabaseRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AlarmDatabaseRepositoryImp @Inject constructor(
    private val globalAlarmDao: GlobalAlarmDao
): AlarmDatabaseRepository {

    override suspend fun insertGlobalAlarm(globalAlarm: GlobalAlarm) {
        globalAlarmDao.insertGlobalAlarm(globalAlarm)
    }

    override suspend fun updateGlobalAlarm(globalAlarm: GlobalAlarm) {
        globalAlarmDao.updateGlobalAlarm(globalAlarm)
    }

    override suspend fun getGlobalAlarmByType(alarmType: String): GlobalAlarm? {
        return globalAlarmDao.getGlobalAlarmByType(alarmType)
    }

    override fun getAllGlobalAlarms(): Flow<List<GlobalAlarm>> {
        return globalAlarmDao.getAllGlobalAlarms()
    }
}