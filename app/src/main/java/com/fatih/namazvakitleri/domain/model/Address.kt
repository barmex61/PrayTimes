package com.fatih.namazvakitleri.domain.model

data class Address(
    val latitude: Double?,
    val longitude: Double?,
    val country: String?,
    val city: String?,
    val district: String?,
    val street: String?,
    val fullAddress: String?
)
