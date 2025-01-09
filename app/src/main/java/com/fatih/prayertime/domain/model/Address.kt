package com.fatih.prayertime.domain.model

//address'i güncelle city district gibi şeylere gerek yok screenden alırsın
data class Address(
    val latitude: Double?,
    val longitude: Double?,
    val country: String?,
    val city: String?,
    val district: String?,
    val street: String?,
    val fullAddress: String?,
    val id : Int = 0 ,
)

