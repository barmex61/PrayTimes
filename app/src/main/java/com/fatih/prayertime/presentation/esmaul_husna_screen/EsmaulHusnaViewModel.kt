package com.fatih.prayertime.presentation.esmaul_husna_screen

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fatih.prayertime.domain.model.EsmaulHusna
import com.fatih.prayertime.domain.repository.LocalDataRepository
import com.fatih.prayertime.domain.use_case.esmaul_husna_use_case.GetEsmaulHusnaUseCase
import com.fatih.prayertime.util.model.state.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EsmaulHusnaViewModel @Inject constructor(
    getEsmaulHusnaUseCase: GetEsmaulHusnaUseCase,
) : ViewModel() {

    val esmaulHusnaState = mutableStateOf(getEsmaulHusnaUseCase())
} 