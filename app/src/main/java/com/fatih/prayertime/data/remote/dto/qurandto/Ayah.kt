package com.fatih.prayertime.data.remote.dto.qurandto

data class Ayah(
    val audio: String,
    val audioSecondary: List<String>,
    val hizbQuarter: Int,
    val juz: Int,
    val manzil: Int,
    val number: Int,
    val numberInSurah: Int,
    val page: Int,
    val ruku: Int,
    val sajda: Boolean,
    val text: String,
    var textTransliteration : String? = null,
    var textTranslation: String? = null
)