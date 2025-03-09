package com.fatih.prayertime.domain.use_case.favorites_use_cases

import com.fatih.prayertime.domain.repository.FavoritesRepository
import javax.inject.Inject

class IsFavoriteUseCase @Inject constructor(
    private val repository: FavoritesRepository
) {
    suspend operator fun invoke(itemId: Long,itemType : String): Boolean {
        return repository.isFavorite(itemId,itemType)
    }
} 