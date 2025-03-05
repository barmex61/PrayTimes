package com.fatih.prayertime.presentation.compass_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fatih.prayertime.domain.use_case.location_use_cases.GetCurrentLocationUseCase
import com.fatih.prayertime.domain.use_case.qibla_use_cases.CalculateQiblaDirectionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CompassScreenViewModel @Inject constructor(
    private val calculateQiblaDirectionUseCase: CalculateQiblaDirectionUseCase,
    private val getCurrentLocationUseCase: GetCurrentLocationUseCase
) : ViewModel() {

    private val _qiblaDirection = MutableStateFlow(0.0)
    val qiblaDirection: StateFlow<Double> = _qiblaDirection


    init {
        viewModelScope.launch(Dispatchers.IO){
            getCurrentLocationUseCase().collectLatest{ location ->
                _qiblaDirection.emit(calculateQiblaDirectionUseCase(location.latitude, location.longitude))
            }
        }
    }
}