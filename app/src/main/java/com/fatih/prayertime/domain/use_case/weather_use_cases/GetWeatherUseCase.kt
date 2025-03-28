package com.fatih.prayertime.domain.use_case.weather_use_cases

import com.fatih.prayertime.domain.model.Weather
import com.fatih.prayertime.domain.repository.WeatherRepository
import com.fatih.prayertime.util.model.state.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetWeatherUseCase @Inject constructor(
    private val weatherRepository: WeatherRepository
) {
    suspend fun getByCoordinates(latitude: Double, longitude: Double): Flow<Resource<Weather>> {
        return weatherRepository.getWeatherByCoordinates(latitude, longitude)
    }
} 