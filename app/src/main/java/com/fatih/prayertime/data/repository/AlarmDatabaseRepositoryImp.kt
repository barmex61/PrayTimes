package com.fatih.prayertime.data.repository

import com.fatih.prayertime.data.local.dao.AlarmDao
import com.fatih.prayertime.data.local.entity.AlarmTimes
import com.fatih.prayertime.domain.repository.AlarmDatabaseRepository
import javax.inject.Inject

class AlarmDatabaseRepositoryImp @Inject constructor(
    private val alarmDao: AlarmDao
): AlarmDatabaseRepository {
    override suspend fun insertAlarmTimes(alarmTimes: AlarmTimes) {
        alarmDao.insertAlarmTimes(alarmTimes)
    }

    override suspend fun getAlarmTimesById(id: Int): AlarmTimes {
        return alarmDao.getAlarmTimesById(id)
    }
}