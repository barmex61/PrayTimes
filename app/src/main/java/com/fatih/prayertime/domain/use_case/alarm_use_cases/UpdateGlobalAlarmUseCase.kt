package com.fatih.prayertime.domain.use_case.alarm_use_cases

import com.fatih.prayertime.domain.model.PrayerAlarm
import com.fatih.prayertime.domain.repository.AlarmDatabaseRepository
import javax.inject.Inject

class UpdateGlobalAlarmUseCase @Inject constructor(private val alarmDatabaseRepository: AlarmDatabaseRepository) {

    suspend operator fun invoke(prayerAlarm: PrayerAlarm) = alarmDatabaseRepository.updateGlobalAlarm(prayerAlarm)

}