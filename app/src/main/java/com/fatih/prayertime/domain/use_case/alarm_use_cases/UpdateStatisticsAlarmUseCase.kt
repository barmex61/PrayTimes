package com.fatih.prayertime.domain.use_case.alarm_use_cases

import com.fatih.prayertime.domain.model.PrayTimes
import com.fatih.prayertime.domain.repository.AlarmDatabaseRepository
import javax.inject.Inject

class UpdateStatisticsAlarmUseCase @Inject constructor(private val alarmDatabaseRepository: AlarmDatabaseRepository) {
    suspend operator fun invoke(prayTimes : PrayTimes) = alarmDatabaseRepository.updateStatisticsAlarm(prayTimes)
}