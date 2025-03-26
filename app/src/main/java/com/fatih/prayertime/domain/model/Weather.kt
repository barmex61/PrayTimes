package com.fatih.prayertime.domain.model

data class Weather(
    val temperature: Double,
    val feelsLike: Double,
    val humidity: Int,
    val windSpeed: Double,
    val condition: String,
    val conditionIcon: String,
    val conditionCode: Int,
    val isDay: Boolean,
    val uvIndex: Double,
    val locationName: String,
    val region: String,
    val country: String,
    val localTime: String
) 