package com.fatih.prayertime.util.model.state

import com.fatih.prayertime.domain.model.Weather

data class WeatherState(
    val city: String = "",
    val weather: Weather? = null,
    val isWeatherLoading: Boolean = false,
    val weatherError: String? = null
)
