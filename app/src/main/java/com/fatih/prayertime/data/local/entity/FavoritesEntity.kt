package com.fatih.prayertime.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorites")
data class FavoritesEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val type: String, // "dua", "hadis"
    val itemId: String,
    val title: String,
    val content: String,
    val timestamp: Long = System.currentTimeMillis()
) 