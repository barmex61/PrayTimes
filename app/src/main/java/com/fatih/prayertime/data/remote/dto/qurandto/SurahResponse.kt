package com.fatih.prayertime.data.remote.dto.qurandto

data class SurahResponse(
    val code: Int,
    val data: SurahInfo,
    val status: String
)