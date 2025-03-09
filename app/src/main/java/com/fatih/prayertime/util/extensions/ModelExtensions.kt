package com.fatih.prayertime.util.extensions

import com.fatih.prayertime.data.remote.dto.hadithdto.Edition
import com.fatih.prayertime.data.remote.dto.hadithdto.HadithEdition
import com.fatih.prayertime.data.remote.dto.islamicdaysdto.IslamicDaysDataDTO
import com.fatih.prayertime.data.remote.dto.praytimesdto.MonthlyPrayTimesResponseDTO
import com.fatih.prayertime.data.remote.dto.praytimesdto.PrayDataDTO
import com.fatih.prayertime.data.remote.dto.praytimesdto.PrayTimesDTO
import com.fatih.prayertime.domain.model.Address
import com.fatih.prayertime.domain.model.IslamicDaysData
import com.fatih.prayertime.domain.model.PrayData
import com.fatih.prayertime.domain.model.PrayTimes
import com.fatih.prayertime.domain.use_case.formatted_use_cases.FormattedUseCase
import com.fatih.prayertime.util.model.enums.PrayTimesString
import org.threeten.bp.LocalDateTime

private val formattedUseCase = FormattedUseCase()

fun MonthlyPrayTimesResponseDTO.toPrayTimes(address: Address): List<PrayTimes> {
    return this.data.map {
        it.toPrayData(address).prayTimes
    }
}

fun PrayDataDTO.toPrayData(address: Address): PrayData = PrayData(
    prayTimes = this.timings.toPrayTimes(this.date.gregorian.date, address)
)

fun PrayTimesDTO.toPrayTimes(date: String, address: Address): PrayTimes = PrayTimes(
    morning = this.Fajr.substring(0, 5),
    noon = this.Dhuhr.substring(0, 5),
    afternoon = this.Asr.substring(0, 5),
    evening = this.Maghrib.substring(0, 5),
    night = this.Isha.substring(0, 5),
    date = date,
    dateLong = formattedUseCase.formatLocalDateToLong(formattedUseCase.formatDDMMYYYYDateToLocalDate(date)),
    latitude = address.latitude,
    longitude = address.longitude,
    country = address.country,
    city = address.city,
    district = address.district,
    street = address.street,
    fullAddress = address.fullAddress,
)

fun PrayTimes.toList(): List<Pair<String, String>> = listOf(
    Pair(PrayTimesString.Morning.name, this.morning),
    Pair(PrayTimesString.Noon.name, this.noon),
    Pair(PrayTimesString.Afternoon.name, this.afternoon),
    Pair(PrayTimesString.Evening.name, this.evening),
    Pair(PrayTimesString.Night.name, this.night)
)

fun PrayTimes.toAddress(): Address = Address(
    latitude = this.latitude,
    longitude = this.longitude,
    country = this.country,
    city = this.city,
    district = this.district,
    street = this.street,
    fullAddress = this.fullAddress
)

fun PrayTimes.localDateTime(time : String): LocalDateTime {
    val localDateTimeStr = "$date $time:00"
    return formattedUseCase.formatDDMMYYYYHHMMDateToLocalDateTime(localDateTimeStr)
}


fun List<IslamicDaysDataDTO>.toIslamicDaysData(): List<IslamicDaysData> {
    if (this.isEmpty()) return emptyList()
    return this.map {
        it.toIslamicDaysData()
    }
}

fun IslamicDaysDataDTO.toIslamicDaysData(): IslamicDaysData = IslamicDaysData(
    date = this.gregorian.date,
    islamicDay = try {
        val islamicDays = mutableListOf<String>()
        this.hijri.holidays.forEach { holiday ->
            val holidayString = holiday.toString()
            islamicDays.add(holidayString)
        }
        islamicDays
    } catch (e: Exception) {
        listOf()
    },
    islamicMonth = hijri.month.en
)

fun HadithEdition.toList(): List<Edition> = listOf(
    this.abudawud,
    this.bukhari,
    this.ibnmajah,
    this.malik,
    this.muslim,
    this.nasai,
    this.tirmidhi,
    this.nawawi,
    this.qudsi,
    this.dehlawi
) 