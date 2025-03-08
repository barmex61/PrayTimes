package com.fatih.prayertime.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "prayer_statistics")
data class PrayerStatisticsEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val prayerName: String,
    val date: String,
    val isCompleted: Boolean,
    val completedTime: Long? = null,
    val isOnTime: Boolean = false,
    val notes: String? = null
) 