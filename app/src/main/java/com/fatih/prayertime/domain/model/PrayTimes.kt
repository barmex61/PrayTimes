package com.fatih.prayertime.domain.model

data class PrayTimes(
    val morning : Pair<String,String>,
    // val sunrise :  Pair<String,String>,
    val noon :  Pair<String,String>,
    val afternoon :  Pair<String,String>,
    val evening :  Pair<String,String>,
    val night :  Pair<String,String>,
)
