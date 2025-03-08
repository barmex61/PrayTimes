package com.fatih.prayertime.presentation.favorites_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fatih.prayertime.data.local.entity.FavoritesEntity
import com.fatih.prayertime.domain.use_case.favorites_use_cases.GetAllFavoritesUseCase
import com.fatih.prayertime.domain.use_case.favorites_use_cases.RemoveFavoriteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val getAllFavoritesUseCase: GetAllFavoritesUseCase,
    private val removeFavoriteUseCase: RemoveFavoriteUseCase
) : ViewModel() {

    private val _selectedType = MutableStateFlow("dua")
    val selectedType = _selectedType.asStateFlow()

    val favorites = _selectedType.flatMapLatest { type ->
        getAllFavoritesUseCase(type)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun setType(type: String) {
        _selectedType.value = type
    }

    fun removeFromFavorites(favorite: FavoritesEntity) {
        viewModelScope.launch {
            removeFavoriteUseCase(favorite)
        }
    }
} 