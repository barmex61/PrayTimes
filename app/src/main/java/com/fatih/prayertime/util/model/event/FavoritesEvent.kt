package com.fatih.prayertime.util.model.event

import com.fatih.prayertime.data.local.entity.FavoritesEntity
import com.fatih.prayertime.util.model.enums.FavoritesType

sealed class FavoritesEvent {
    data class SetType(val favoritesType: String) : FavoritesEvent()
    data class RemoveFavorite(val favorite: FavoritesEntity) : FavoritesEvent()
}