package com.fatih.prayertime.domain.use_case.alarm_use_cases

import com.fatih.prayertime.domain.model.PrayTimes
import com.fatih.prayertime.domain.repository.AlarmDatabaseRepository
import javax.inject.Inject

class UpdateStatisticsAlarmUseCase @Inject constructor(private val alarmDatabaseRepository: AlarmDatabaseRepository) {
    fun updateStatisticsAlarms(prayTimes : PrayTimes) = alarmDatabaseRepository.updateStatisticsAlarmForPrayTime(prayTimes)
    fun updateStatisticsAlarm(prayTime: Long,alarmDate: String,alarmType: String) = alarmDatabaseRepository.updateStatisticsAlarmForPrayType(prayTime,alarmDate,alarmType)
}