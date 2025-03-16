package com.fatih.prayertime.data.remote.dto.qurandto

data class SurahInfoListResponse(
    val code: Int,
    val data: List<SurahInfo>,
    val status: String
)