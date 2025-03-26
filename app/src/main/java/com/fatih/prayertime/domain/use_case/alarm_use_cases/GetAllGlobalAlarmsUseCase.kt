package com.fatih.prayertime.domain.use_case.alarm_use_cases

import com.fatih.prayertime.domain.model.PrayerAlarm
import com.fatih.prayertime.domain.repository.AlarmDatabaseRepository
import com.fatih.prayertime.util.model.enums.PrayTimesString
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class GetAllGlobalAlarmsUseCase @Inject constructor(
    private val alarmDatabaseRepository: AlarmDatabaseRepository,
    private val insertGlobalAlarmUseCase: InsertGlobalAlarmUseCase
){
    operator fun invoke() : Flow<List<PrayerAlarm>>  {
        val globalAlarms = alarmDatabaseRepository.getAllGlobalAlarms()

        return alarmDatabaseRepository.getAllGlobalAlarms()
    }
}