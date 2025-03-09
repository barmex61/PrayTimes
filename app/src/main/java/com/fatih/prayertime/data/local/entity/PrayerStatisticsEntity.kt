package com.fatih.prayertime.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "prayer_statistics")
data class PrayerStatisticsEntity(
    @PrimaryKey(autoGenerate = false)
    val id: Int,
    val prayerType: String,
    val date: String,
    val isCompleted: Boolean,
)