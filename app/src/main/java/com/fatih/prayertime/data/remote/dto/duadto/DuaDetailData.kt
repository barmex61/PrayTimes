package com.fatih.prayertime.data.remote.dto.duadto

data class DuaDetailData(
    val arabic: String,
    val category: String,
    val categoryName: String,
    val fawaid: String,
    val id: Int,
    val latin: String,
    val notes: String,
    val source: String,
    val title: String,
    val translation: String
)