package com.fatih.prayertime.util.extensions

import com.fatih.prayertime.R
import com.fatih.prayertime.data.remote.dto.hadithdto.Edition
import com.fatih.prayertime.data.remote.dto.hadithdto.HadithEdition
import com.fatih.prayertime.data.remote.dto.islamicdaysdto.IslamicDaysDataDTO
import com.fatih.prayertime.data.remote.dto.praytimesdto.MonthlyPrayTimesResponseDTO
import com.fatih.prayertime.data.remote.dto.praytimesdto.PrayTimesDTO
import com.fatih.prayertime.data.remote.dto.qurandto.QuranApiData
import com.fatih.prayertime.domain.model.Address
import com.fatih.prayertime.domain.model.IslamicDaysData
import com.fatih.prayertime.domain.model.PrayTimes
import com.fatih.prayertime.domain.use_case.formatted_use_cases.FormattedUseCase
import com.fatih.prayertime.util.model.enums.PlaybackMode
import com.fatih.prayertime.util.model.enums.PrayTimesString
import com.fatih.prayertime.util.model.state.AudioInfo
import com.fatih.prayertime.util.utils.QuranUtils
import com.fatih.prayertime.util.utils.QuranUtils.SURAH_VERSE_RANGES
import org.threeten.bp.LocalDateTime

private val formattedUseCase = FormattedUseCase()

fun MonthlyPrayTimesResponseDTO.toPrayTimes(address: Address): List<PrayTimes> {
    return this.data.map {
        it.timings.toPrayTimes(it.date.gregorian.date, address, it.meta.method.id)
    }
}


fun PrayTimesDTO.toPrayTimes(date: String, address: Address, methodId: Int? = null): PrayTimes = PrayTimes(
    morning = this.Fajr.substring(0, 5),
    sunrise = this.Sunrise.substring(0,5),
    imsak = this.Imsak.substring(0,5),
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
    method = methodId
)

fun PrayTimes.toPrayTimeInfoList(): List<PrayTimesInfo> = listOf(
    PrayTimesInfo(R.drawable.imsak, PrayTimesString.Imsak.stringResId, this.imsak),
    PrayTimesInfo(R.drawable.morning, PrayTimesString.Morning.stringResId, this.morning),
    PrayTimesInfo(R.drawable.surnise, PrayTimesString.Sunrise.stringResId, this.sunrise),
    PrayTimesInfo(R.drawable.noon, PrayTimesString.Noon.stringResId, this.noon),
    PrayTimesInfo(R.drawable.afternoon, PrayTimesString.Afternoon.stringResId, this.afternoon),
    PrayTimesInfo(R.drawable.evening, PrayTimesString.Evening.stringResId, this.evening),
    PrayTimesInfo(R.drawable.night, PrayTimesString.Night.stringResId, this.night),

)

data class PrayTimesInfo(
    val drawable : Int,
    val stringRes : Int,
    val time : String
)

fun PrayTimes.toTimeList() : List<String> = listOf(
    this.morning,
    this.noon,
    this.afternoon,
    this.evening,
    this.night
)

fun PrayTimes.toPrayTimePair(offsetMinutes : Long? = null) : List<Pair<String, Long>> {
    val offsetMs = if (offsetMinutes != null) offsetMinutes * 1000L * 60L else 0L
    val result = listOf(
        PrayTimesString.Morning.name to formattedUseCase.formatHHMMtoLong(this.morning, formattedUseCase.formatDDMMYYYYDateToLocalDate(this.date)) + offsetMs,
        PrayTimesString.Noon.name to formattedUseCase.formatHHMMtoLong(this.noon, formattedUseCase.formatDDMMYYYYDateToLocalDate(this.date)) + offsetMs,
        PrayTimesString.Afternoon.name to formattedUseCase.formatHHMMtoLong(this.afternoon, formattedUseCase.formatDDMMYYYYDateToLocalDate(this.date)) + offsetMs,
        PrayTimesString.Evening.name to formattedUseCase.formatHHMMtoLong(this.evening, formattedUseCase.formatDDMMYYYYDateToLocalDate(this.date)) + offsetMs,
        PrayTimesString.Night.name to formattedUseCase.formatHHMMtoLong(this.night, formattedUseCase.formatDDMMYYYYDateToLocalDate(this.date)) + offsetMs
    )
    
    result.forEach { (prayerType, time) ->
        val formattedTime = formattedUseCase.formatLongToLocalDateTime(time)
        println("İstatistik alarmı: $prayerType için $formattedTime zamanında ayarlandı (Ofset: ${offsetMinutes ?: 0} dakika)")
    }
    
    return result
}

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

fun PrayTimes.nextPrayTimeLong(currentPrayTime : String) : Long {
    return when(currentPrayTime){
        PrayTimesString.Morning.name ->{
            formattedUseCase.formatHHMMtoLong(this.noon, formattedUseCase.formatDDMMYYYYDateToLocalDate(this.date))
        }
        PrayTimesString.Noon.name ->{
            formattedUseCase.formatHHMMtoLong(this.afternoon, formattedUseCase.formatDDMMYYYYDateToLocalDate(this.date))
        }
        PrayTimesString.Afternoon.name ->{
            formattedUseCase.formatHHMMtoLong(this.evening, formattedUseCase.formatDDMMYYYYDateToLocalDate(this.date))
        }
        PrayTimesString.Evening.name ->{
            formattedUseCase.formatHHMMtoLong(this.night, formattedUseCase.formatDDMMYYYYDateToLocalDate(this.date))
        }
        PrayTimesString.Night.name ->{
            formattedUseCase.formatHHMMtoLong(this.morning, formattedUseCase.formatDDMMYYYYDateToLocalDate(this.date))
        }
        else -> 0L
    }
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

fun HadithEdition.toNameAndTimePair(): List<Edition> = listOf(
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

fun QuranApiData.toText(): String = this.englishName

fun AudioInfo.getNextOrPreviousAudioInfo(direction: Int): AudioInfo {
    return when (playbackMode) {
        PlaybackMode.VERSE_STREAM -> {
            val newAyahNumber = (ayahNumber + direction).coerceIn(1, 6236)
            val newSurahNumber = SURAH_VERSE_RANGES.entries.find { (_, range) ->
                newAyahNumber in range
            }?.key ?: surahNumber
            val surahName = QuranUtils.turkishNames.keys.withIndex().find { key ->
                newSurahNumber == key.index + 1
            }?.value ?: ""
            copy(
                surahNumber = newSurahNumber,
                ayahNumber = newAyahNumber,
                surahName = surahName
            )
        }
        PlaybackMode.SURAH -> {
            val newSurahNumber = (surahNumber + direction).coerceIn(1, 114)
            val surahName = QuranUtils.turkishNames.keys.withIndex().find { key ->
                newSurahNumber == key.index + 1
            }?.value ?: ""
            copy(
                surahNumber = newSurahNumber,
                surahName = surahName
            )
        }
    }
}

fun AudioInfo.getExactAudioInfo(number: Int): AudioInfo {
    return when (playbackMode) {
        PlaybackMode.VERSE_STREAM -> {
            val newAyahNumber = number.coerceIn(1, 6236)
            val newSurahNumber = SURAH_VERSE_RANGES.entries.find { (_, range) ->
                newAyahNumber in range
            }?.key ?: surahNumber
            copy(
                surahNumber = newSurahNumber,
                ayahNumber = newAyahNumber
            )
        }
        PlaybackMode.SURAH -> this
    }
}