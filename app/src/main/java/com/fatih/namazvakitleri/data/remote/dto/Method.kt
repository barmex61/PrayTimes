package com.fatih.namazvakitleri.data.remote.dto

data class Method(
    val id: Int,
    val location: Location,
    val name: String,
    val params: Params
)