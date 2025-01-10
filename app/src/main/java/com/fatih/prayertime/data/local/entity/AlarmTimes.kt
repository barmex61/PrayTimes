package com.fatih.prayertime.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class AlarmTimes(
    val morning : Pair<Boolean,Long?>,
    val noon :  Pair<Boolean,Long?>,
    val afternoon :  Pair<Boolean,Long?>,
    val evening :  Pair<Boolean,Long?>,
    val night :  Pair<Boolean,Long?>,
    @PrimaryKey(autoGenerate = false)
    val id : Int = 0,
)
