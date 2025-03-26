package com.fatih.prayertime.presentation.favorites_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fatih.prayertime.data.local.entity.FavoritesEntity
import com.fatih.prayertime.domain.use_case.favorites_use_cases.GetAllFavoritesUseCase
import com.fatih.prayertime.domain.use_case.favorites_use_cases.RemoveFavoriteUseCase
import com.fatih.prayertime.util.model.enums.FavoritesType
import com.fatih.prayertime.util.model.event.FavoritesEvent
import com.fatih.prayertime.util.model.state.FavoritesScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val getAllFavoritesUseCase: GetAllFavoritesUseCase,
    private val removeFavoriteUseCase: RemoveFavoriteUseCase,
) : ViewModel() {

    private val _favoritesState = MutableStateFlow(FavoritesScreenState())
    val favoritesState = _favoritesState.asStateFlow()

    private val _favoritesEvent = MutableSharedFlow<FavoritesEvent>()

    @OptIn(ExperimentalCoroutinesApi::class)
    val favorites = _favoritesState.map {
        it.favoritesType
    }.distinctUntilChanged().flatMapLatest {
        getAllFavoritesUseCase(it)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    fun onEvent(event: FavoritesEvent) = viewModelScope.launch{
        _favoritesEvent.emit(event)
    }

    private fun handleEvent(event: FavoritesEvent){
        when(event){
            is FavoritesEvent.SetType -> _favoritesState.value = _favoritesState.value.copy(favoritesType = event.favoritesType)
            is FavoritesEvent.RemoveFavorite -> {
                viewModelScope.launch {
                    removeFavoriteUseCase(event.favorite)
                    _favoritesEvent.emit(FavoritesEvent.ShowMessage("Favorilerden çıkarıldı"))
                }
            }
            is FavoritesEvent.ShowMessage -> {}
        }
    }

    init {
        viewModelScope.launch {
            _favoritesEvent.collectLatest { event ->
                handleEvent(event)
            }
        }
    }

} 