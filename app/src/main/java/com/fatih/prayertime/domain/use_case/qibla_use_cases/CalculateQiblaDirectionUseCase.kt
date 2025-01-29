package com.fatih.prayertime.domain.use_case.qibla_use_cases

import javax.inject.Inject
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

class CalculateQiblaDirectionUseCase @Inject constructor() {

    operator fun invoke(latitude: Double, longitude: Double): Double{
        val kaabaLat = 21.4225
        val kaabaLong = 39.8262

        val deltaLong = Math.toRadians(kaabaLong - longitude)
        val currentLatRad = Math.toRadians(latitude)
        val kaabaLatRad = Math.toRadians(kaabaLat)

        val y = sin(deltaLong) * cos(kaabaLatRad)
        val x = cos(currentLatRad) * sin(kaabaLatRad) - sin(currentLatRad) * cos(kaabaLatRad) * cos(deltaLong)

        val qiblaDirection = atan2(y, x)
        return (Math.toDegrees(qiblaDirection) + 360) % 360
    }
}