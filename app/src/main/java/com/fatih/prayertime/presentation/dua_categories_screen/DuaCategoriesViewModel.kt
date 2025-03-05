package com.fatih.prayertime.presentation.dua_categories_screen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fatih.prayertime.data.remote.dto.duadto.DuaCategories
import com.fatih.prayertime.data.remote.dto.duadto.DuaCategoryDetail
import com.fatih.prayertime.domain.use_case.dua_use_cases.get_dua_categories.GetDuaCategoriesUseCase
import com.fatih.prayertime.domain.use_case.dua_use_cases.get_dua_details.GetDuaDetailsUseCase
import com.fatih.prayertime.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DuaCategoriesViewModel @Inject constructor(
    private val getDuaCategoriesUseCase: GetDuaCategoriesUseCase,
    private val getDuaCategoryDetailUseCase: GetDuaDetailsUseCase
) : ViewModel() {

    private val _duaCategories = MutableStateFlow<Resource<DuaCategories>>(Resource.loading())
    val duaCategories = _duaCategories

    private val _duaCategoryDetail = MutableStateFlow<Resource<DuaCategoryDetail>>(Resource.loading())
    val duaCategoryDetail = _duaCategoryDetail

    private val _duaDetailPath = MutableStateFlow<String>("")
    val duaDetailPath = _duaDetailPath

    private fun getDuaCategories() = viewModelScope.launch(Dispatchers.IO){
        _duaCategories.emit(Resource.loading())
        _duaCategories.emit(getDuaCategoriesUseCase())
    }

    fun updateDetailPath(detailPath : String) = viewModelScope.launch(Dispatchers.Default){
        _duaDetailPath.emit(detailPath)
    }

    private fun getDuaCategoryDetail(detailPath: String) = viewModelScope.launch(Dispatchers.IO){
        _duaCategoryDetail.emit(Resource.loading())
        _duaCategoryDetail.emit(getDuaCategoryDetailUseCase(detailPath))
    }

    init {
        getDuaCategories()
        viewModelScope.launch(Dispatchers.IO){
            _duaDetailPath.collectLatest { path ->
                if (path.isNotEmpty()) { // Boş path'leri gereksiz yere işlememek için
                    Log.d("HadithCollectionViewModel", "Collection path: $path")
                    getDuaCategoryDetail(path)
                }
            }
        }
    }
}