package com.fatih.prayertime.util

import com.fatih.prayertime.data.local.entity.AlarmTimes
import com.fatih.prayertime.data.remote.dto.DailyPrayResponseDTO
import com.fatih.prayertime.data.remote.dto.PrayDataDTO
import com.fatih.prayertime.data.remote.dto.PrayTimesDTO
import com.fatih.prayertime.domain.model.Address
import com.fatih.prayertime.domain.model.DailyPrayResponse
import com.fatih.prayertime.domain.model.PrayData
import com.fatih.prayertime.domain.model.PrayTimes

fun DailyPrayResponseDTO.toPrayTimes(address: Address) : PrayTimes {
    val dailyPrayResponse = this.toDailyPrayResponse(address)
    return dailyPrayResponse.data.prayTimes
}

fun DailyPrayResponseDTO.toDailyPrayResponse(address: Address) : DailyPrayResponse =
    DailyPrayResponse(data = this.data.toPrayData(address))

fun PrayDataDTO.toPrayData(address: Address) : PrayData = PrayData(
    prayTimes = this.timings.toPrayTimes(this.date.gregorian.date,address)
)

fun PrayTimesDTO.toPrayTimes(date : String,address: Address) : PrayTimes = PrayTimes(
    morning = Pair("Morning",this.Fajr) ,
    //sunrise = Pair("Sunrise",this.Sunrise),
    noon = Pair("Noon",this.Dhuhr),
    afternoon = Pair("Afternoon",this.Asr),
    evening = Pair("Evening",this.Maghrib),
    night = Pair("Night",this.Isha),
    date = date,
    latitude = address.latitude,
    longitude = address.longitude,
    country = address.country,
    city = address.city,
    district = address.district,
    street = address.street,
    fullAddress = address.fullAddress,
    time = System.currentTimeMillis()
)

fun PrayTimes.toList() : List<Pair<String,String>> = listOf(
    this.morning,
    //this.sunrise,
    this.noon,
    this.afternoon,
    this.evening,
    this.night
)

fun PrayTimes.toAddress() : Address = Address(
    latitude = this.latitude,
    longitude = this.longitude,
    country = this.country,
    city = this.city,
    district = this.district,
    street = this.street,
    fullAddress = this.fullAddress
)

fun String?.convertTimeToSeconds(): Int {
    this?.let { timeString ->
        val timeParts = timeString.split(":")
        if (timeParts.size in 2..3) { // Saat ve dakika olmalı, saniye isteğe bağlı
            try {
                val hours = timeParts[0].toInt()
                val minutes = timeParts[1].toInt()
                val seconds = if (timeParts.size == 3) timeParts[2].toInt() else 0

                return hours * 3600 + minutes * 60 + seconds
            } catch (e: NumberFormatException) {
                return 0
            }
        } else {
            return 0
        }
    }
    return 0
}

fun AlarmTimes.toHashMap() : HashMap<String,Pair<Boolean,Long?>> = hashMapOf(
    "Morning" to this.morning,
    "Noon" to this.noon,
    "Afternoon" to this.afternoon,
    "Evening" to this.evening,
    "Night" to this.night
)