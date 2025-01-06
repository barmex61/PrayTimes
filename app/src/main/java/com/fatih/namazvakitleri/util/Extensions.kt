package com.fatih.namazvakitleri.util

import android.content.Context
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.fatih.namazvakitleri.R
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
    //sunrise = Pair("Sunrise",this.Sunrise),
    noon = Pair("Noon",this.Dhuhr),
    afternoon = Pair("Afternoon",this.Asr),
    evening = Pair("Evening",this.Maghrib),
    night = Pair("Night",this.Isha)
)

val prayIcons = mapOf(
    "Morning" to Icons.Default.ShoppingCart,
    //"Sunrise" to Icons.Default.ShoppingCart,
    "Noon" to Icons.Default.ShoppingCart,
    "Afternoon" to Icons.Default.ShoppingCart,
    "Evening" to Icons.Default.ShoppingCart,
    "Night" to Icons.Default.ShoppingCart
)


fun PrayTimes.toPrayList() : List<Triple<String,String,ImageVector>> =
    listOf(morning, /*sunrise,*/ noon, afternoon, evening, night).map { (prayerName, prayerTime) ->
        Triple(prayerName, prayerTime, prayIcons[prayerName]!!)
    }