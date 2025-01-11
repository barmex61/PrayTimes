package com.fatih.prayertime.domain.use_case.get_global_alarm_by_type_use_case

import com.fatih.prayertime.domain.repository.AlarmDatabaseRepository
import javax.inject.Inject

class GetGlobalAlarmByTypeUseCase @Inject constructor(private val alarmDatabaseRepository: AlarmDatabaseRepository) {
    suspend operator fun invoke(alarmType : String) = alarmDatabaseRepository.getGlobalAlarmByType(alarmType)
}