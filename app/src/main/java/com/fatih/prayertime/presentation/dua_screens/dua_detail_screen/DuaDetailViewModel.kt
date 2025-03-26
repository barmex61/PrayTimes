package com.fatih.prayertime.presentation.dua_screens.dua_detail_screen

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fatih.prayertime.data.local.entity.FavoritesEntity
import com.fatih.prayertime.data.remote.dto.duadto.DuaCategoryDetail
import com.fatih.prayertime.domain.use_case.dua_use_case.GetDuaUseCase
import com.fatih.prayertime.domain.use_case.favorites_use_cases.AddFavoriteUseCase
import com.fatih.prayertime.domain.use_case.favorites_use_cases.IsFavoriteUseCase
import com.fatih.prayertime.domain.use_case.favorites_use_cases.RemoveFavoriteUseCase
import com.fatih.prayertime.util.extensions.withRetry
import com.fatih.prayertime.util.model.enums.FavoritesType
import com.fatih.prayertime.util.utils.HadithUtils.generateFavoriteItemId
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class DuaDetailViewModel @Inject constructor(
    private val isFavoriteUseCase: IsFavoriteUseCase,
    private val removeFavoriteUseCase: RemoveFavoriteUseCase,
    private val addFavoriteUseCase: AddFavoriteUseCase,
    getDuaUseCase: GetDuaUseCase
) : ViewModel(){

    private val retryTrigger = MutableSharedFlow<Unit>()
    private val duaId = MutableStateFlow(0)
    private val _duaCategoryId = MutableStateFlow(0)
    private val duaState = mutableStateOf(getDuaUseCase())

    fun updateDuaId(id : Int) = viewModelScope.launch(Dispatchers.Default){
        duaId.emit(id)
    }
    val duaDetail = duaId.combine(_duaCategoryId){ duaId , duaCategoryId ->
        Pair(duaId,duaCategoryId)
    }.map {
        duaState.value?.data?.firstOrNull { it.id == _duaCategoryId.value }?.detail?.firstOrNull { it.id == duaId.value }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(1000),null)

    val isFavorite = duaDetail.filterNotNull().withRetry(retryTrigger).map { duaDetail ->
        val itemId = generateFavoriteItemId(type = FavoritesType.DUA.name, title = duaDetail.titleTr,null,null,null)
        isFavoriteUseCase(itemId,FavoritesType.DUA.name)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(1000),false)


    fun updateDuaCategoryId(index: Int) = viewModelScope.launch(Dispatchers.Default){
        _duaCategoryId.emit(index)
    }

    fun toggleFavorite() = viewModelScope.launch(Dispatchers.IO){
        duaDetail.value?.let { dua ->
            val id = generateFavoriteItemId(type = FavoritesType.DUA.name, title = dua.titleTr,null,null,null)
            if (isFavorite.value) {
                removeFavoriteUseCase(
                    FavoritesEntity(
                        type = FavoritesType.DUA.name,
                        duaCategoryId = _duaCategoryId.value,
                        duaId = duaId.value,
                        title = if (Locale.getDefault().language == "tr") dua.titleTr else dua.title,
                        itemId = id,
                        content = dua.arabic,
                        latin = dua.latin
                    )
                )
            } else {
                addFavoriteUseCase(
                    FavoritesEntity(
                        type = FavoritesType.DUA.name,
                        duaCategoryId = _duaCategoryId.value,
                        duaId = duaId.value,
                        title = if (Locale.getDefault().language == "tr") dua.titleTr else dua.title,
                        itemId = id,
                        content = dua.arabic,
                        latin = dua.latin
                    )
                )
            }

        }
        retryTrigger.emit(Unit)

    }

}