package com.fatih.prayertime.domain.use_case.insert_alarm_times_use_case

import com.fatih.prayertime.data.local.entity.AlarmTimes
import com.fatih.prayertime.domain.repository.AlarmDatabaseRepository
import javax.inject.Inject

class InsertAlarmTimesUseCase @Inject constructor(private val alarmDatabaseRepository: AlarmDatabaseRepository) {

    suspend operator fun invoke(alarmTimes : AlarmTimes) = alarmDatabaseRepository.insertAlarmTimes(alarmTimes)
}