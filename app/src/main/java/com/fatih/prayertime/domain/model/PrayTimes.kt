package com.fatih.prayertime.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class PrayTimes(
    @PrimaryKey
    val id : Int = 0,
    val date : String,
    val morning : Pair<String,String>,
    // val sunrise :  Pair<String,String>,
    val noon :  Pair<String,String>,
    val afternoon :  Pair<String,String>,
    val evening :  Pair<String,String>,
    val night :  Pair<String,String>,
)
