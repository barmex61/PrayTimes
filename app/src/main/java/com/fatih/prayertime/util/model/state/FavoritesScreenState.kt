package com.fatih.prayertime.util.model.state

import com.fatih.prayertime.util.model.enums.FavoritesType

data class FavoritesScreenState(
    val favoritesType: String = FavoritesType.DUA.name,
    val isLoading : Boolean = false,
    val error : String? = null
)
