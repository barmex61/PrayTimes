package com.fatih.prayertime.presentation.main_screen

import com.fatih.prayertime.domain.model.Address
import com.fatih.prayertime.domain.model.PrayTimes
import com.fatih.prayertime.domain.model.Weather
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MainScreenStateManager @Inject constructor() {
    
    private val _addressState = MutableStateFlow<Address?>(null)
    val addressState: StateFlow<Address?> = _addressState.asStateFlow()
    
    private val _prayTimesState = MutableStateFlow<PrayTimes?>(null)
    val prayTimesState: StateFlow<PrayTimes?> = _prayTimesState.asStateFlow()
    
    private val _weatherState = MutableStateFlow<Weather?>(null)
    val weatherState: StateFlow<Weather?> = _weatherState.asStateFlow()
    
    suspend fun updateAddress(address: Address?) {
        _addressState.emit(address)
    }
    
    suspend fun updatePrayTimes(prayTimes: PrayTimes?) {
        _prayTimesState.emit(prayTimes)
    }
    
    suspend fun updateWeather(weather: Weather?) {
        _weatherState.emit(weather)
    }
}