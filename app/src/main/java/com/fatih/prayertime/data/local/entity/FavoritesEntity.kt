package com.fatih.prayertime.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorites")
data class FavoritesEntity(
    val type: String, // "dua", "hadis"
    @PrimaryKey(autoGenerate = false)
    val itemId: Int,
    val title: String,
    val content: String,
    val latin : String? = null,
    val duaCategoryIndex : Int? = null,
    val timestamp: Long = System.currentTimeMillis()
) 