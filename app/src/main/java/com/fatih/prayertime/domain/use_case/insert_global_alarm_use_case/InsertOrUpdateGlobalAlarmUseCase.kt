package com.fatih.prayertime.domain.use_case.insert_global_alarm_use_case

import com.fatih.prayertime.data.local.entity.GlobalAlarm
import com.fatih.prayertime.domain.repository.AlarmDatabaseRepository
import javax.inject.Inject

class InsertGlobalAlarmUseCase @Inject constructor(private val alarmDatabaseRepository: AlarmDatabaseRepository) {

    suspend operator fun invoke(globalAlarm : GlobalAlarm) = alarmDatabaseRepository.insertGlobalAlarm(globalAlarm)
}