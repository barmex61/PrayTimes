package com.fatih.prayertime.presentation.dua_category_detail_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fatih.prayertime.data.remote.dto.duadto.DuaCategoryDetail
import com.fatih.prayertime.util.Constants.duaCategory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DuaCategoryDetailViewModel @Inject constructor() : ViewModel() {

    private val _duaCategoryDetailList : MutableStateFlow<List<DuaCategoryDetail>?> = MutableStateFlow(null)
    val duaCategoryDetailList = _duaCategoryDetailList

    private val duaCategoryDetailIndex = MutableStateFlow(0)

    private fun getDuaCategoryDetailList(index:Int) = viewModelScope.launch(Dispatchers.Default){
        _duaCategoryDetailList.emit(duaCategory?.data?.getOrNull(index)?.detail)
    }

    fun updateDuaCategoryDetailIndex(index: Int) = viewModelScope.launch(Dispatchers.Default){
        duaCategoryDetailIndex.emit(index)
    }

    init {
        viewModelScope.launch(Dispatchers.Default){
            duaCategoryDetailIndex.collectLatest {
                getDuaCategoryDetailList(it)
            }
        }
    }

}