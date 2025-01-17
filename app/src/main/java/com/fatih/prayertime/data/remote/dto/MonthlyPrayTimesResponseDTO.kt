package com.fatih.prayertime.data.remote.dto

data class MonthlyPrayTimesResponseDTO(
    val code: Int,
    val data: List<PrayDataDTO>,
    val status: String
)