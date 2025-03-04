package com.fatih.prayertime.data.remote.dto.hadithdto

data class Hadith(
    val arabicnumber: Float,
    val grades: List<Grade>,
    val hadithnumber: Float,
    val reference: Reference,
    val text: String
)