package com.fatih.prayertime.data.remote.dto.praytimesdto

data class DailyPrayResponseDTO(
    val code: Int,
    val data: PrayDataDTO,
    val status: String
)