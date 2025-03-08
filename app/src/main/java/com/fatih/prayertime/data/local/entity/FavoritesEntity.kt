package com.fatih.prayertime.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorites")
data class FavoritesEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val type: String, // "dua", "hadis"
    val itemId: Int,
    val title: String,
    val content: String,
    val latin : String? = null,
    val duaCategoryIndex : Int? = null,
    val timestamp: Long = System.currentTimeMillis()
) 