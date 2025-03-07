package com.fatih.prayertime.data.remote.dto.duadto

data class DuaCategoryData(
    val name: String,
    val slug: String,
    val total: Int,
    var nameTr : String? = null
)