package com.fatih.prayertime.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class PrayTimes(
    @PrimaryKey(autoGenerate = true)
    val id : Int = 0,
    val date : String,
    val time : Long,
    val morning : Pair<String,String>,
    // val sunrise :  Pair<String,String>,
    val noon :  Pair<String,String>,
    val afternoon :  Pair<String,String>,
    val evening :  Pair<String,String>,
    val night :  Pair<String,String>,
    val latitude: Double,
    val longitude: Double,
    val country: String?,
    val city: String?,
    val district: String?,
    val street: String?,
    val fullAddress: String?,
)
