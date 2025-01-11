package com.fatih.prayertime.domain.use_case.alarm_use_cases.get_all_global_alarms_use_case

import com.fatih.prayertime.domain.repository.AlarmDatabaseRepository
import javax.inject.Inject

class GetAllGlobalAlarmsUseCase @Inject constructor(
    private val alarmDatabaseRepository: AlarmDatabaseRepository
){
    operator fun invoke() = alarmDatabaseRepository.getAllGlobalAlarms()
}