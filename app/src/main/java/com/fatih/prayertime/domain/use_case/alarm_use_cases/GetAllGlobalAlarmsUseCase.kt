package com.fatih.prayertime.domain.use_case.alarm_use_cases

import com.fatih.prayertime.domain.repository.AlarmDatabaseRepository
import javax.inject.Inject

class GetAllGlobalAlarmsUseCase @Inject constructor(
    private val alarmDatabaseRepository: AlarmDatabaseRepository
){
    operator fun invoke() = alarmDatabaseRepository.getAllGlobalAlarms()
}