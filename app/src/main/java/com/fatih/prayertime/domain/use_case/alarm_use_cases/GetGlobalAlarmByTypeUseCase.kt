package com.fatih.prayertime.domain.use_case.alarm_use_cases

import com.fatih.prayertime.domain.repository.AlarmDatabaseRepository
import javax.inject.Inject

class GetGlobalAlarmByTypeUseCase @Inject constructor(private val alarmDatabaseRepository: AlarmDatabaseRepository) {
    suspend operator fun invoke(alarmType : String) = alarmDatabaseRepository.getPrayerAlarmByType(alarmType)
}