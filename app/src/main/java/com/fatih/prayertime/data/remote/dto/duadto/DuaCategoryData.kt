package com.fatih.prayertime.data.remote.dto.duadto

data class DuaCategoryData(
    val name: String,
    val id : Int,
    val total: Int,
    var nameTr : String ,
    val detail: List<DuaCategoryDetail>
)