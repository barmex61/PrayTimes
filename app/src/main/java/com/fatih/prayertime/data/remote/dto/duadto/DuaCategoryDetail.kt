package com.fatih.prayertime.data.remote.dto.duadto

data class DuaCategoryDetail(
    val code: String,
    val data: List<DuaCategoryDetailData>,
    val statusCode: Int
)