package com.fatih.prayertime.presentation.dua_detail_screen;

import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fatih.prayertime.data.local.entity.FavoritesEntity
import com.fatih.prayertime.data.remote.dto.duadto.DuaCategoryDetail
import com.fatih.prayertime.domain.use_case.favorites_use_cases.AddFavoriteUseCase
import com.fatih.prayertime.domain.use_case.favorites_use_cases.IsFavoriteUseCase
import com.fatih.prayertime.domain.use_case.favorites_use_cases.RemoveFavoriteUseCase
import com.fatih.prayertime.util.Constants
import com.fatih.prayertime.util.FavoritesType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Locale

@HiltViewModel
class DuaDetailViewModel @Inject constructor(
    private val addFavoriteUseCase: AddFavoriteUseCase,
    private val removeFavoriteUseCase: RemoveFavoriteUseCase,
    private val isFavoriteUseCase: IsFavoriteUseCase
) : ViewModel() {

    private val _duaDetail = MutableStateFlow<DuaCategoryDetail?>(null)
    val duaDetail = _duaDetail.asStateFlow()

    private val _isFavorite = MutableStateFlow(false)
    val isFavorite = _isFavorite.asStateFlow()

    private val duaCategoryIndex = MutableStateFlow(0)
    private val duaId = MutableStateFlow(0)

    fun updateDuaId(duaId: Int) = viewModelScope.launch(Dispatchers.Default){
        this@DuaDetailViewModel.duaId.emit(duaId)
        loadDuaDetail(duaId)
    }

    fun updateCategoryIndex(categoryIndex : Int) = viewModelScope.launch(Dispatchers.Default){
        duaCategoryIndex.emit(categoryIndex)
    }

    private fun loadDuaDetail(duaId: Int) = viewModelScope.launch(Dispatchers.IO){
        _duaDetail.value = Constants.duaCategory!!.data[duaCategoryIndex.value].detail.firstOrNull { it.id == duaId }
        checkIsFavorite(duaId)
    }

    private fun checkIsFavorite(duaId: Int) = viewModelScope.launch(Dispatchers.IO){
        _isFavorite.value = isFavoriteUseCase(duaId,FavoritesType.DUA.name)
    }

    fun toggleFavorite() = viewModelScope.launch(Dispatchers.IO){
        _duaDetail.value?.let { dua ->
            if (_isFavorite.value) {
                removeFavoriteUseCase(
                    FavoritesEntity(
                        type = FavoritesType.DUA.name,
                        duaCategoryIndex = duaCategoryIndex.value,
                        title = if (Locale.getDefault().language == "tr") dua.titleTr else dua.title,
                        itemId = dua.id,
                        content = dua.arabic,
                        latin = dua.latin
                    )
                )
            } else {
                addFavoriteUseCase(
                    FavoritesEntity(
                        type = FavoritesType.DUA.name,
                        duaCategoryIndex = duaCategoryIndex.value,
                        title = if (Locale.getDefault().language == "tr") dua.titleTr else dua.title,
                        itemId = dua.id,
                        content = dua.arabic,
                        latin = dua.latin
                    )
                )
            }
            _isFavorite.value = !_isFavorite.value
        }

    }

} 