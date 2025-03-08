package com.fatih.prayertime.domain.use_case.favorites_use_cases

import com.fatih.prayertime.data.local.entity.FavoritesEntity
import com.fatih.prayertime.domain.repository.FavoritesRepository
import javax.inject.Inject

class AddFavoriteUseCase @Inject constructor(
    private val repository: FavoritesRepository
) {
    suspend operator fun invoke(favorite: FavoritesEntity) {
        repository.addToFavorites(favorite)
    }
} 