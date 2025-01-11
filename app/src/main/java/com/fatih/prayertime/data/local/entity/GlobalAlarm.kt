package com.fatih.prayertime.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class GlobalAlarm(
    @PrimaryKey(autoGenerate = false)
    val alarmType: String,
    val alarmTime: String,
    val isEnabled: Boolean,
    val alarmOffset: Int
)