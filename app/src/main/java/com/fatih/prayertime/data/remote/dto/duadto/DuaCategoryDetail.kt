package com.fatih.prayertime.data.remote.dto.duadto

data class DuaDetail(
    val category: String,
    val categoryName: String,
    val id: Int,
    val title: String,
    val fawaid: String,
    val arabic : String,
    val titleTr: String,
    val latin: String,
    val notes: String,
    val source: String,
    val translation: String
)