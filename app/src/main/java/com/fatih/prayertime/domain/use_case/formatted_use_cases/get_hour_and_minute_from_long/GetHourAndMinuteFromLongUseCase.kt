package com.fatih.prayertime.domain.use_case.formatted_use_cases.get_hour_and_minute_from_long


import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class GetHourAndMinuteFromLongUseCase @Inject constructor(){

    operator fun invoke(time : Long) : Pair<Int,Int> {
        val date = Date(time)
        val dateFormatter = SimpleDateFormat("HH:mm", Locale.getDefault())
        val list = dateFormatter.format(date).split(":")
        val hour = list[0].toInt()
        val minute = list[1].toInt()
        return Pair(hour,minute)
    }
}