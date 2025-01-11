package com.fatih.prayertime.domain.use_case.update_global_alarm_use_case

import com.fatih.prayertime.domain.model.GlobalAlarm
import com.fatih.prayertime.domain.repository.AlarmDatabaseRepository
import javax.inject.Inject

class UpdateGlobalAlarmUseCase @Inject constructor(private val alarmDatabaseRepository: AlarmDatabaseRepository) {

    suspend operator fun invoke(globalAlarm: GlobalAlarm) = alarmDatabaseRepository.updateGlobalAlarm(globalAlarm)


}