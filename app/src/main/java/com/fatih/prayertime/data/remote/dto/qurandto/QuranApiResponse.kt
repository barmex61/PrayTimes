package com.fatih.prayertime.data.remote.dto.qurandto

data class QuranApiResponse(
    val code: Int,
    val data: List<QuranApiData>,
    val status: String
)