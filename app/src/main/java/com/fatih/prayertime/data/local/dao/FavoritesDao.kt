package com.fatih.prayertime.data.local.dao

import androidx.room.*
import com.fatih.prayertime.data.local.entity.FavoritesEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoritesDao {
    @Query("SELECT * FROM favorites ORDER BY timestamp DESC")
    fun getAllFavorites(): Flow<List<FavoritesEntity>>

    @Query("SELECT * FROM favorites WHERE type = :type ORDER BY timestamp DESC")
    fun getFavoritesByType(type: String): Flow<List<FavoritesEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(favorite: FavoritesEntity)

    @Delete
    suspend fun deleteFavorite(favorite: FavoritesEntity)

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE itemId = :itemId AND type = :itemType)")
    suspend fun isFavorite(itemId: Long, itemType: String): Boolean
} 