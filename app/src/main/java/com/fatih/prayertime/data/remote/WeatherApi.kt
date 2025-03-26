package com.fatih.prayertime.data.remote

import com.fatih.prayertime.data.remote.dto.WeatherResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {
    @GET("v1/current.json")
    suspend fun getCurrentWeather(
        @Query("key") apiKey: String,
        @Query("q") location: String,
        @Query("aqi") airQuality: String = "no"
    ): WeatherResponse
    
    @GET("v1/current.json")
    suspend fun getCurrentWeatherByCoordinates(
        @Query("key") apiKey: String,
        @Query("q") coordinates: String,
        @Query("aqi") airQuality: String = "no"
    ): WeatherResponse
} 