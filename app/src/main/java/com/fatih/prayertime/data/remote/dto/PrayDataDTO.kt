package com.fatih.prayertime.data.remote.dto

data class PrayDataDTO(
    val date: Date,
    val meta: Meta,
    val timings: PrayTimesDTO
)