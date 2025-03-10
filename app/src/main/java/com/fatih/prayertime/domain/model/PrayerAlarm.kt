package com.fatih.prayertime.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity
@Serializable
data class PrayerAlarm(
    @PrimaryKey(autoGenerate = false)
    val alarmType: String,
    val alarmTime: Long,
    val alarmTimeString : String,
    val isEnabled: Boolean,
    val alarmOffset: Long,
    val soundUri: String? = null
)