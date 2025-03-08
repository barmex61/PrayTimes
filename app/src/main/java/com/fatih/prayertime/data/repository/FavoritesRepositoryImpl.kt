package com.fatih.prayertime.data.repository

import com.fatih.prayertime.data.local.dao.FavoritesDao
import com.fatih.prayertime.data.local.entity.FavoritesEntity
import com.fatih.prayertime.domain.repository.FavoritesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FavoritesRepositoryImpl @Inject constructor(
    private val favoritesDao: FavoritesDao
) : FavoritesRepository {
    override fun getAllFavorites(): Flow<List<FavoritesEntity>> {
        return favoritesDao.getAllFavorites()
    }

    override fun getFavoritesByType(type: String): Flow<List<FavoritesEntity>> {
        return favoritesDao.getFavoritesByType(type)
    }

    override suspend fun addToFavorites(favorite: FavoritesEntity) {
        favoritesDao.insertFavorite(favorite)
    }

    override suspend fun removeFromFavorites(favorite: FavoritesEntity) {
        favoritesDao.deleteFavorite(favorite)
    }

    override suspend fun isFavorite(itemId: Int,itemType : String): Boolean {
        return favoritesDao.isFavorite(itemId,itemType)
    }
} 