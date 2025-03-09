package com.fatih.prayertime.util.utils

import com.fatih.prayertime.domain.model.PrayTimes
import com.fatih.prayertime.domain.use_case.formatted_use_cases.FormattedUseCase
import com.fatih.prayertime.util.model.enums.PrayTimesString

object AlarmUtils {
    fun getAlarmTimeForPrayTimes(
        dailyPrayTimes: PrayTimes,
        alarmType: String,
        alarmOffset: Long,
        formattedUseCase: FormattedUseCase
    ): String {
        val alarmTimeWithoutOffset = when (alarmType) {
            PrayTimesString.Morning.name -> dailyPrayTimes.morning
            PrayTimesString.Noon.name -> dailyPrayTimes.noon
            PrayTimesString.Afternoon.name -> dailyPrayTimes.afternoon
            PrayTimesString.Evening.name -> dailyPrayTimes.evening
            PrayTimesString.Night.name -> dailyPrayTimes.night
            else -> "00:00"
        }
        return formattedUseCase.minusMinutesFromTime(alarmTimeWithoutOffset, alarmOffset)
    }

    fun getAlarmTimeLongForPrayTimes(prayTimes: PrayTimes,formattedUseCase: FormattedUseCase) : List<Long>{
        val morningTime = formattedUseCase.formatHHMMtoLong(prayTimes.morning, formattedUseCase.formatDDMMYYYYDateToLocalDate(prayTimes.date))
        val noonTime = formattedUseCase.formatHHMMtoLong(prayTimes.noon, formattedUseCase.formatDDMMYYYYDateToLocalDate(prayTimes.date))
        val afternoonTime = formattedUseCase.formatHHMMtoLong(prayTimes.afternoon, formattedUseCase.formatDDMMYYYYDateToLocalDate(prayTimes.date))
        val eveningTime = formattedUseCase.formatHHMMtoLong(prayTimes.evening, formattedUseCase.formatDDMMYYYYDateToLocalDate(prayTimes.date))
        val nightTime = formattedUseCase.formatHHMMtoLong(prayTimes.night, formattedUseCase.formatDDMMYYYYDateToLocalDate(prayTimes.date))

        return listOf(morningTime, noonTime, afternoonTime, eveningTime, nightTime)
    }
} 