package com.fatih.prayertime.data.remote.dto.islamicdaysdto

import com.fatih.prayertime.data.remote.dto.Gregorian
import com.fatih.prayertime.data.remote.dto.Hijri

data class IslamicDaysData(
    val gregorian: Gregorian,
    val hijri: Hijri
)