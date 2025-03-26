package com.fatih.prayertime.presentation.dua_screens.dua_categories_screen

import androidx.compose.runtime.mutableStateOf
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

import androidx.lifecycle.ViewModel
import com.fatih.prayertime.domain.use_case.dua_use_case.GetDuaUseCase

@HiltViewModel
class DuaCategoriesViewModel @Inject constructor(
    getDuaUseCase: GetDuaUseCase
) : ViewModel() {

    val duaState = mutableStateOf(getDuaUseCase())

} 