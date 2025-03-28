package com.fatih.prayertime.data.repository

import com.fatih.prayertime.data.remote.WeatherApi
import com.fatih.prayertime.data.remote.dto.WeatherResponse
import com.fatih.prayertime.domain.model.Weather
import com.fatih.prayertime.domain.repository.WeatherRepository
import com.fatih.prayertime.util.config.ApiConfig.WEATHER_API_KEY
import com.fatih.prayertime.util.model.state.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeatherRepositoryImpl @Inject constructor(
    private val weatherApi: WeatherApi
) : WeatherRepository {
    

    
    override suspend fun getWeatherByCoordinates(latitude: Double, longitude: Double): Flow<Resource<Weather>> = flow {
        emit(Resource.loading())

        try {
            val coordinates = "$latitude,$longitude"
            val response = weatherApi.getCurrentWeatherByCoordinates(
                apiKey = WEATHER_API_KEY,
                coordinates = coordinates
            )
            emit(Resource.success(response.toWeather(latitude,longitude)))
        } catch (e: HttpException) {
            emit(Resource.error("Hava durumu alınamadı: ${e.message()}"))
        } catch (e: IOException) {
            emit(Resource.error("İnternet bağlantınızı kontrol edin: ${e.message}"))
        } catch (e: Exception) {
            emit(Resource.error("Beklenmeyen hata: ${e.message}"))
        }
    }.flowOn(Dispatchers.IO)
    
    private fun WeatherResponse.toWeather(latitude: Double, longitude: Double): Weather {
        return Weather(
            temperature = current.tempC,
            feelsLike = current.feelsLikeC,
            humidity = current.humidity,
            windSpeed = current.windKph,
            condition = current.condition.text,
            conditionIcon = "https:${current.condition.icon}",
            conditionCode = current.condition.code,
            isDay = current.isDay == 1,
            uvIndex = current.uvIndex,
            locationName = location.name,
            region = location.region,
            country = location.country,
            localTime = location.localTime,
            latitude = latitude,
            longitude = longitude
        )
    }
} 