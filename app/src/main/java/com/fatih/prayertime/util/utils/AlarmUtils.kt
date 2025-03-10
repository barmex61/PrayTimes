package com.fatih.prayertime.util.utils

import android.content.Context
import com.fatih.prayertime.R
import com.fatih.prayertime.domain.model.PrayTimes
import com.fatih.prayertime.domain.use_case.formatted_use_cases.FormattedUseCase
import com.fatih.prayertime.util.model.enums.FavoritesType
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

    fun getContentTitleForPrayType(prayType : String,context: Context) = when(prayType){
        PrayTimesString.Morning.name -> context.getString(R.string.did_u_pray_the_morning_prayer)
        PrayTimesString.Noon.name -> context.getString(R.string.did_u_pray_the_noon_prayer)
        PrayTimesString.Afternoon.name -> context.getString(R.string.did_u_pray_the_afternoon_prayer)
        PrayTimesString.Evening.name -> context.getString(R.string.did_u_pray_the_evening_prayer)
        PrayTimesString.Night.name -> context.getString(R.string.did_u_pray_the_night_prayer)
        else -> ""
    }
} 