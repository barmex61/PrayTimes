package com.fatih.prayertime.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class GlobalAlarm(
    @PrimaryKey(autoGenerate = false)
    val alarmType: String,
    val alarmTime: Long,
    val alarmTimeString : String,
    val isEnabled: Boolean,
    val alarmOffset: Long
)