package com.fatih.prayertime.presentation.compass_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fatih.prayertime.domain.use_case.location_use_cases.GetCurrentLocationUseCase
import com.fatih.prayertime.domain.use_case.qibla_use_cases.CalculateQiblaDirectionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CompassScreenViewModel @Inject constructor(
    private val calculateQiblaDirectionUseCase: CalculateQiblaDirectionUseCase,
    getCurrentLocationUseCase: GetCurrentLocationUseCase
) : ViewModel() {

    val qiblaDirection = getCurrentLocationUseCase.invoke().filterNotNull().map { location ->
        calculateQiblaDirectionUseCase(location.latitude, location.longitude)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), 0.0)

}