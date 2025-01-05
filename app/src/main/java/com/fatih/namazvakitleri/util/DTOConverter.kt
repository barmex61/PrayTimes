package com.fatih.namazvakitleri.util

import com.fatih.namazvakitleri.data.remote.dto.DailyPrayResponseDTO
import com.fatih.namazvakitleri.data.remote.dto.PrayDataDTO
import com.fatih.namazvakitleri.data.remote.dto.PrayTimesDTO
import com.fatih.namazvakitleri.domain.model.DailyPrayResponse
import com.fatih.namazvakitleri.domain.model.PrayData
import com.fatih.namazvakitleri.domain.model.PrayTimes

fun DailyPrayResponseDTO.toDailyPrayResponse() : DailyPrayResponse =
    DailyPrayResponse(data = this.data.toPrayData())

fun PrayDataDTO.toPrayData() : PrayData = PrayData(prayTimes = this.timings.toPrayTimes() )

fun PrayTimesDTO.toPrayTimes() : PrayTimes = PrayTimes(
    morning = Pair("Morning",this.Fajr) ,
    sunrise = Pair("Sunrise",this.Sunrise),
    noon = Pair("Noon",this.Dhuhr),
    afternoon = Pair("Sabah",this.Asr),
    evening = Pair("Sabah",this.Maghrib),
    night = Pair("Sabah",this.Isha)
)