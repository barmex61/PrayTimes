package com.fatih.prayertime.data.remote.dto.praytimesdto

data class MonthlyPrayTimesResponseDTO(
    val code: Int,
    val data: List<PrayDataDTO>,
    val status: String
)