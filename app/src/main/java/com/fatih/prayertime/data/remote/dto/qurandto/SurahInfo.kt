package com.fatih.prayertime.data.remote.dto.qurandto

data class SurahInfo(
    val englishName: String,
    val englishNameTranslation: String,
    val name: String,
    val number: Int,
    val numberOfAyahs: Int,
    val revelationType: String
)