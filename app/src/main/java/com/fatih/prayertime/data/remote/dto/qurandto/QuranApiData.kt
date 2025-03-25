package com.fatih.prayertime.data.remote.dto.qurandto

data class QuranApiData(
    val direction: String?,
    val bitrate : Int? ,
    val englishName: String,
    val format: String,
    val identifier: String,
    val language: String,
    val name: String,
    val type: String
)