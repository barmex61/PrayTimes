package com.fatih.prayertime.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class PrayTimes(
    @PrimaryKey(autoGenerate = true)
    val id : Int = 0,
    val date : String,
    val dateLong : Long,
    val createdAt: Long = System.currentTimeMillis(),
    val morning : String,
    val imsak : String,
    val sunrise :  String,
    val noon : String,
    val afternoon :  String,
    val evening :  String,
    val night :  String,
    val latitude: Double,
    val longitude: Double,
    val country: String?,
    val city: String?,
    val district: String?,
    val street: String?,
    val fullAddress: String?,
    val method: Int? = null,
)
