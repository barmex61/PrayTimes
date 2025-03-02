package com.fatih.prayertime.data.remote.dto.islamicdaysdto

import com.fatih.prayertime.data.remote.dto.praytimesdto.Gregorian
import com.fatih.prayertime.data.remote.dto.praytimesdto.Hijri

data class IslamicDaysDataDTO(
    val gregorian: Gregorian,
    val hijri: Hijri
)