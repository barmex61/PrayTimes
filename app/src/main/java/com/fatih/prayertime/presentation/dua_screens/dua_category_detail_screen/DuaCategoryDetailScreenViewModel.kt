package com.fatih.prayertime.presentation.dua_screens.dua_category_detail_screen

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fatih.prayertime.domain.use_case.dua_use_case.GetDuaUseCase
import com.fatih.prayertime.util.extensions.withRetry
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DuaCategoryDetailScreenViewModel @Inject constructor(
    getDuaUseCase: GetDuaUseCase
): ViewModel(){

    private val _duaCategoryId = MutableStateFlow(0)
    val duaCategoryId = _duaCategoryId

    private val retryTrigger = MutableSharedFlow<Unit>()

    val duaCategoryDetailList =_duaCategoryId.withRetry(retryTrigger).map { categoryId->
        duaState.value?.data?.firstOrNull { it.id == categoryId }?.detail
    }.stateIn(viewModelScope,SharingStarted.WhileSubscribed(1000),null)

    private val duaState = mutableStateOf(getDuaUseCase())

    fun updateDuaCategoryId(index: Int) = viewModelScope.launch(Dispatchers.Default){
        _duaCategoryId.emit(index)
    }

    fun retryDuaCategoryDetailLoading()=viewModelScope.launch {
        retryTrigger.emit(Unit)
    }

}