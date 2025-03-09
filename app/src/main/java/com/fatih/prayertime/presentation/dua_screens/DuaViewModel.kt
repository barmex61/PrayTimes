package com.fatih.prayertime.presentation.dua_screens;

import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fatih.prayertime.data.local.entity.FavoritesEntity
import com.fatih.prayertime.data.remote.dto.duadto.Dua
import com.fatih.prayertime.data.remote.dto.duadto.DuaCategoryDetail
import com.fatih.prayertime.domain.use_case.dua_use_case.GetDuaUseCase
import com.fatih.prayertime.domain.use_case.dua_use_case.LoadDuaUseCase
import com.fatih.prayertime.domain.use_case.favorites_use_cases.AddFavoriteUseCase
import com.fatih.prayertime.domain.use_case.favorites_use_cases.IsFavoriteUseCase
import com.fatih.prayertime.domain.use_case.favorites_use_cases.RemoveFavoriteUseCase
import com.fatih.prayertime.util.extensions.generateItemId
import com.fatih.prayertime.util.model.enums.FavoritesType
import com.fatih.prayertime.util.model.state.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Locale

@HiltViewModel
class DuaViewModel @Inject constructor(
    private val addFavoriteUseCase: AddFavoriteUseCase,
    private val removeFavoriteUseCase: RemoveFavoriteUseCase,
    private val isFavoriteUseCase: IsFavoriteUseCase,
    private val loadDuaUseCase: LoadDuaUseCase,
    private val getDuaUseCase: GetDuaUseCase
) : ViewModel() {

    private val _duaDetail = MutableStateFlow<DuaCategoryDetail?>(null)
    val duaDetail = _duaDetail.asStateFlow()

    private val _isFavorite = MutableStateFlow(false)
    val isFavorite = _isFavorite.asStateFlow()

    private val duaId = MutableStateFlow(0)

    fun updateDuaId(id : Int) = viewModelScope.launch(Dispatchers.Default){
        duaId.emit(id)
    }

    private fun getDuaDetail() = viewModelScope.launch(Dispatchers.IO){
        _duaDetail.value = _duaState.value.data!!.data.firstOrNull { it.id == _duaCategoryId.value }?.detail?.firstOrNull { it.id == duaId.value }
        checkIsFavorite()
    }

    private fun checkIsFavorite() = viewModelScope.launch(Dispatchers.IO){
        duaDetail.value ?: return@launch
        val id = generateItemId(type = FavoritesType.DUA.name, title = duaDetail.value!!.titleTr,null,null,null)
        _isFavorite.value = isFavoriteUseCase(id,FavoritesType.DUA.name)
    }

    fun toggleFavorite() = viewModelScope.launch(Dispatchers.IO){
        _duaDetail.value?.let { dua ->
            val id = generateItemId(type = FavoritesType.DUA.name, title = dua.titleTr,null,null,null)
            if (_isFavorite.value) {
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
            _isFavorite.value = !_isFavorite.value
        }

    }

    // --Dua Category Detail Screen

    private val _duaCategoryDetailList : MutableStateFlow<List<DuaCategoryDetail>?> = MutableStateFlow(null)
    val duaCategoryDetailList = _duaCategoryDetailList

    private val _duaCategoryId = MutableStateFlow(0)
    val duaCategoryId = _duaCategoryId

    fun getDuaCategoryDetailList() = viewModelScope.launch(Dispatchers.Default){
        _duaCategoryDetailList.emit(duaState.value.data?.data?.firstOrNull { it.id == _duaCategoryId.value }?.detail)
    }

    fun updateDuaCategoryId(index: Int) = viewModelScope.launch(Dispatchers.Default){
        _duaCategoryId.emit(index)
    }

    // -- Dua Category Screen

    private val _duaState = MutableStateFlow<Resource<Dua>>(Resource.loading())
    val duaState: StateFlow<Resource<Dua>> = _duaState.asStateFlow()

    fun loadDua() = viewModelScope.launch {
        try {
            loadDuaUseCase()
            getDuaUseCase().collect { dua->
                _duaState.value = if (dua != null) {
                    Resource.success(dua)
                } else {
                    Resource.error("Dua kategorileri yüklenemedi")
                }
            }
        } catch (e: Exception) {
            _duaState.value = Resource.error("Error occurred : ${e.localizedMessage}")
        }
    }

    init {
        loadDua()
        viewModelScope.launch(Dispatchers.Default){
            launch {
                _duaCategoryId.collectLatest {
                    getDuaCategoryDetailList()
                }
            }
            launch {
                duaId.collectLatest {
                    getDuaDetail()
                }
            }

        }
    }

} 