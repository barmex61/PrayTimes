package com.fatih.prayertime.data.remote.dto.duadto

data class DuaCategories(
    val code: String,
    val data: List<DuaCategoryData>,
    val statusCode: Int
)