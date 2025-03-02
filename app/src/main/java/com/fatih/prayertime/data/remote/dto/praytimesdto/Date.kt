package com.fatih.prayertime.data.remote.dto.praytimesdto

data class Date(
    val gregorian: Gregorian,
    val hijri: Hijri,
    val readable: String,
    val timestamp: String
)