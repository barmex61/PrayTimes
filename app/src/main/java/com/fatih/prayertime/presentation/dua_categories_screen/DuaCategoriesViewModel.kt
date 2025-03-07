package com.fatih.prayertime.presentation.dua_categories_screen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fatih.prayertime.data.remote.dto.duadto.DuaCategories
import com.fatih.prayertime.data.remote.dto.duadto.DuaCategoryDetail
import com.fatih.prayertime.data.remote.dto.duadto.DuaDetail
import com.fatih.prayertime.domain.use_case.dua_use_cases.GetDuaCategoriesUseCase
import com.fatih.prayertime.domain.use_case.dua_use_cases.GetDuaCategoryDetailUseCase
import com.fatih.prayertime.domain.use_case.dua_use_cases.GetDuaDetailUseCase
import com.fatih.prayertime.util.Resource
import com.fatih.prayertime.util.Status
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class DuaCategoriesViewModel @Inject constructor(
    private val getDuaCategoriesUseCase: GetDuaCategoriesUseCase,
    private val getDuaCategoryDetailUseCase: GetDuaCategoryDetailUseCase,
    private val getDuaDetailsUseCase: GetDuaDetailUseCase
) : ViewModel() {

    private val _duaCategories = MutableStateFlow<Resource<DuaCategories>>(Resource.loading())
    val duaCategories = _duaCategories

    private val _duaCategoryDetail = MutableStateFlow<Resource<DuaCategoryDetail>>(Resource.loading())
    val duaCategoryDetail = _duaCategoryDetail

    private val _duaDetail = MutableStateFlow<Resource<DuaDetail>>(Resource.loading())
    val duaDetail = _duaDetail

    private val duaDetailPath = MutableStateFlow("")
    private val duaId = MutableStateFlow(0)

    fun updateDetailPath(detailPath : String) = viewModelScope.launch(Dispatchers.Default){
        duaDetailPath.emit(detailPath)
    }

    fun updateDuaId(id : Int) = viewModelScope.launch(Dispatchers.Default){
        duaId.emit(id)
    }

    fun getDuaCategories() = viewModelScope.launch(Dispatchers.IO) {
        _duaCategories.emit(Resource.loading())
        _duaCategories.emit(getDuaCategoriesUseCase())
    }

    fun getDuaCategoryDetail() = viewModelScope.launch(Dispatchers.IO) {
        _duaCategoryDetail.emit(Resource.loading())
        _duaCategoryDetail.emit(getDuaCategoryDetailUseCase(duaDetailPath.value))
    }

    fun getDuaDetail() = viewModelScope.launch(Dispatchers.IO) {
        _duaDetail.emit(Resource.loading())
        _duaDetail.emit(getDuaDetailsUseCase(duaDetailPath.value,duaId.value))
    }

    init {
        getDuaCategories()
        viewModelScope.launch(Dispatchers.IO){
            duaDetailPath.collectLatest { path ->
                if (path.isNotEmpty()) { // Boş path'leri gereksiz yere işlememek için
                    Log.d("HadithCollectionViewModel", "Collection path: $path")
                    getDuaCategoryDetail()
                }
            }
        }
        viewModelScope.launch(Dispatchers.IO){
            duaId.collectLatest { id ->
                if (id != 0){
                    getDuaDetail()
                }
            }
        }
    }
}