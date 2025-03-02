package com.fatih.prayertime.data.remote.dto.praytimesdto

data class Gregorian(
    val date: String,
    val day: String,
    val designation: Designation,
    val format: String,
    val lunarSighting: Boolean,
    val month: Month,
    val weekday: Weekday,
    val year: String
)