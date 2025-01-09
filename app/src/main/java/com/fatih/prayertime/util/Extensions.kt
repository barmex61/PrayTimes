package com.fatih.prayertime.util

import com.fatih.prayertime.data.local.AddressEntity
import com.fatih.prayertime.data.remote.dto.DailyPrayResponseDTO
import com.fatih.prayertime.data.remote.dto.PrayDataDTO
import com.fatih.prayertime.data.remote.dto.PrayTimesDTO
import com.fatih.prayertime.domain.model.Address
import com.fatih.prayertime.domain.model.DailyPrayResponse
import com.fatih.prayertime.domain.model.PrayData
import com.fatih.prayertime.domain.model.PrayTimes

fun DailyPrayResponseDTO.toPrayTimes() : PrayTimes {
    val dailyPrayResponse = this.toDailyPrayResponse()
    return dailyPrayResponse.data.prayTimes
}

fun DailyPrayResponseDTO.toDailyPrayResponse() : DailyPrayResponse =
    DailyPrayResponse(data = this.data.toPrayData())

fun PrayDataDTO.toPrayData() : PrayData = PrayData(
    prayTimes = this.timings.toPrayTimes(this.date.gregorian.date)
)

fun PrayTimesDTO.toPrayTimes(date : String) : PrayTimes = PrayTimes(
    morning = Pair("Morning",this.Fajr) ,
    //sunrise = Pair("Sunrise",this.Sunrise),
    noon = Pair("Noon",this.Dhuhr),
    afternoon = Pair("Afternoon",this.Asr),
    evening = Pair("Evening",this.Maghrib),
    night = Pair("Night",this.Isha),
    date = date
)

fun PrayTimes.toList() : List<Pair<String,String>> = listOf(
    this.morning,
    //this.sunrise,
    this.noon,
    this.afternoon,
    this.evening,
    this.night
)

fun AddressEntity.toAddress() : Address = Address(
    latitude = this.latitude,
    longitude = this.longitude,
    country = this.country,
    city = this.city,
    district = this.district,
    fullAddress = this.fullAddress,
    street = this.street
)

fun Address.toAddressEntity() : AddressEntity = AddressEntity(
    latitude = this.latitude,
    longitude = this.longitude,
    country = this.country,
    city = this.city,
    district = this.district,
    street = this.street,
    fullAddress = this.fullAddress
)