package com.fatih.prayertime.presentation.main_screen

import com.fatih.prayertime.domain.model.Prayer
import com.fatih.prayertime.domain.model.Weather

data class MainScreenState(
    val prayers: List<Prayer> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val city: String = "",
    val weather: Weather? = null,
    val isWeatherLoading: Boolean = false,
    val weatherError: String? = null
) 