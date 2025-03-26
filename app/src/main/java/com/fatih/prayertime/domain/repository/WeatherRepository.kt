package com.fatih.prayertime.domain.repository

import com.fatih.prayertime.domain.model.Weather
import com.fatih.prayertime.util.model.state.Resource
import kotlinx.coroutines.flow.Flow

interface WeatherRepository {
    suspend fun getCurrentWeather(location: String): Flow<Resource<Weather>>
    suspend fun getCurrentWeatherByCoordinates(latitude: Double, longitude: Double): Flow<Resource<Weather>>
} 