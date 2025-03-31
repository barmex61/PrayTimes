package com.fatih.prayertime.domain.use_case.alarm_use_cases

import com.fatih.prayertime.domain.model.PrayerAlarm
import com.fatih.prayertime.domain.repository.AlarmDatabaseRepository
import com.fatih.prayertime.util.model.enums.PrayTimesString
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class GetAllGlobalAlarmsUseCase @Inject constructor(
    private val alarmDatabaseRepository: AlarmDatabaseRepository,
    private val insertGlobalAlarmUseCase: InsertGlobalAlarmUseCase,
){
    suspend operator fun invoke() : Flow<List<PrayerAlarm>>  {
        val prayerAlarm = alarmDatabaseRepository.getAllPrayerAlarms()
        if (prayerAlarm.first().isEmpty()) {
            val initialAlarms = PrayTimesString.entries.filterIndexed { index, _ ->
                index <= 6
            }.map {
                PrayerAlarm(
                    alarmType = it.name,
                    alarmTime = 0L,
                    alarmTimeString = "Alarm off",
                    isEnabled = false,
                    alarmOffset = 0
                )
            }
            initialAlarms.forEach { alarm->
                insertGlobalAlarmUseCase(alarm)
            }
        }
        return alarmDatabaseRepository.getAllPrayerAlarms()
    }
}