package com.fatih.namazvakitleri.presentation.main_screen.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fatih.namazvakitleri.domain.model.Address
import com.fatih.namazvakitleri.domain.model.DailyPrayResponse
import com.fatih.namazvakitleri.domain.use_case.get_daily_pray_times_use_case.GetDailyPrayTimesUseCase
import com.fatih.namazvakitleri.domain.use_case.get_location_and_adress_use_case.GetLocationAndAddressUseCase
import com.fatih.namazvakitleri.util.Resource
import com.fatih.namazvakitleri.util.Status
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainScreenViewModel @Inject constructor(
    private val getDailyPrayTimesUseCase: GetDailyPrayTimesUseCase,
    private val getLocationAndAddressUseCase: GetLocationAndAddressUseCase
) : ViewModel() {


    //Pray - Times

    private val _dailyPrayTimes = MutableStateFlow<Resource<DailyPrayResponse>>(Resource.loading())
    val dailyPrayTimes: StateFlow<Resource<DailyPrayResponse>> = _dailyPrayTimes

    fun getDailyPrayTimes(date: String, latitude: Double, longitude: Double) = viewModelScope.launch {
        _dailyPrayTimes.emit(Resource.loading())
        _dailyPrayTimes.emit(getDailyPrayTimesUseCase(date, latitude, longitude))
    }

    //Location - Room

    private val _liveAddress = MutableStateFlow<Resource<Address>>(Resource.loading())
    val liveAddress: StateFlow<Resource<Address>> = _liveAddress

    private val _currentAddress = MutableStateFlow<Address?>(null)
    val currentAddress: StateFlow<Address?> = _currentAddress

    fun getLiveAddress() = viewModelScope.launch {
        getLocationAndAddressUseCase().collect { address ->
            _liveAddress.emit(address)
        }
    }

    fun setCurrentAddress(liveAddress: Address) = viewModelScope.launch(Dispatchers.Default) {
        _currentAddress.emit(liveAddress)
    }

}