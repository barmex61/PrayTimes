package com.fatih.namazvakitleri.presentation.main_screen.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fatih.namazvakitleri.domain.model.DailyPrayResponse
import com.fatih.namazvakitleri.domain.use_case.get_daily_pray_times_use_case.GetDailyPrayTimesUseCase
import com.fatih.namazvakitleri.util.LatLong
import com.fatih.namazvakitleri.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainScreenViewModel @Inject constructor(private val getDailyPrayTimesUseCase: GetDailyPrayTimesUseCase) : ViewModel() {

    private val _dailyPrayTimes = MutableStateFlow<Resource<DailyPrayResponse>>(Resource.loading())
    val dailyPrayTimes: StateFlow<Resource<DailyPrayResponse>> = _dailyPrayTimes
    private val _currentPosition = MutableStateFlow<LatLong?>(null)

    fun getDailyPrayTimes(date: String, latitude: Double, longitude: Double) = viewModelScope.launch {
        _dailyPrayTimes.emit(Resource.loading())
        _dailyPrayTimes.emit(getDailyPrayTimesUseCase(date, latitude, longitude))
    }
}