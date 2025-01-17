package com.fatih.prayertime.util

import com.fatih.prayertime.data.remote.dto.MonthlyPrayTimesResponseDTO
import com.fatih.prayertime.data.remote.dto.PrayDataDTO
import com.fatih.prayertime.data.remote.dto.PrayTimesDTO
import com.fatih.prayertime.domain.model.Address
import com.fatih.prayertime.domain.model.PrayData
import com.fatih.prayertime.domain.model.PrayTimes

fun MonthlyPrayTimesResponseDTO.toPrayTimes(address: Address) : List<PrayTimes> {
    return this.data.map {
        it.toPrayData(address).prayTimes
    }
}

fun PrayDataDTO.toPrayData(address: Address) : PrayData = PrayData(
    prayTimes = this.timings.toPrayTimes(this.date.gregorian.date,address)
)

fun PrayTimesDTO.toPrayTimes(date : String,address: Address) : PrayTimes = PrayTimes(
    morning = this.Fajr.substring(0,5),
    //sunrise = this.Sunrise,
    noon = this.Dhuhr.substring(0,5),
    afternoon = this.Asr.substring(0,5),
    evening = this.Maghrib.substring(0,5),
    night = this.Isha.substring(0,5),
    date = date,
    latitude = address.latitude,
    longitude = address.longitude,
    country = address.country,
    city = address.city,
    district = address.district,
    street = address.street,
    fullAddress = address.fullAddress,
)

fun PrayTimes.toList() : List<Pair<String,String>> = listOf(
    Pair(PrayTimesString.Morning.name,this.morning),
    Pair(PrayTimesString.Noon.name,this.noon),
    Pair(PrayTimesString.Afternoon.name,this.afternoon),
    Pair(PrayTimesString.Evening.name,this.evening),
    Pair(PrayTimesString.Night.name,this.night)
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

enum class PrayTimesString {
    Morning,
    Noon,
    Afternoon,
    Evening,
    Night;
}