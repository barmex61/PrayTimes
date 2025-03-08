package com.fatih.prayertime.domain.repository

import com.fatih.prayertime.data.local.entity.FavoritesEntity
import kotlinx.coroutines.flow.Flow

interface FavoritesRepository {
    fun getAllFavorites(): Flow<List<FavoritesEntity>>
    fun getFavoritesByType(type: String): Flow<List<FavoritesEntity>>
    suspend fun addToFavorites(favorite: FavoritesEntity)
    suspend fun removeFromFavorites(favorite: FavoritesEntity)
    suspend fun isFavorite(itemId: Int,itemType : String): Boolean
} 