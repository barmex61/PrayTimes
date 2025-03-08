package com.fatih.prayertime.domain.use_case.favorites_use_cases

import com.fatih.prayertime.data.local.entity.FavoritesEntity
import com.fatih.prayertime.domain.repository.FavoritesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllFavoritesUseCase @Inject constructor(
    private val repository: FavoritesRepository
) {
    operator fun invoke(type: String): Flow<List<FavoritesEntity>> {
        return repository.getFavoritesByType(type)
    }
} 