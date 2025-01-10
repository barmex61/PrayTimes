package com.fatih.prayertime.domain.use_case.get_alarm_times_use_case

import com.fatih.prayertime.domain.repository.AlarmDatabaseRepository
import javax.inject.Inject

class GetAlarmTimesUseCase @Inject constructor(
    private val alarmDatabaseRepository: AlarmDatabaseRepository
){
    suspend operator fun invoke() = alarmDatabaseRepository.getAlarmTimesById()
}