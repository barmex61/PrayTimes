package com.fatih.prayertime.data.remote.dto.qurandto

data class SurahInfo(
    val englishName: String,
    var turkishName : String?,
    val englishNameTranslation: String,
    var turkishNameTranslation : String?,
    val name: String,
    val number: Int,
    val numberOfAyahs: Int,
    val revelationType: String,
    val ayahs: List<Ayah>? = null,
    val edition: Edition? = null,
)