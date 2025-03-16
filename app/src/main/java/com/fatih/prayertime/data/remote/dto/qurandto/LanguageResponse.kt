package com.fatih.prayertime.data.remote.dto.qurandto

data class LanguageResponse(
    val code: Int,
    val data: List<String>,
    val status: String
)