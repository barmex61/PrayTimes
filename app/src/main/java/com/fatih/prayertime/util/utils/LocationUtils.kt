package com.fatih.prayertime.util.utils

import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

object LocationUtils {

    fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val earthRadiusKm = 6371.0

        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)

        val a = sin(dLat / 2).pow(2) + 
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) * 
                sin(dLon / 2).pow(2)
        
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return earthRadiusKm * c
    }
    

    fun isSignificantLocationChange(
        lat1: Double, 
        lon1: Double, 
        lat2: Double, 
        lon2: Double, 
        thresholdKm: Double = 10.0
    ): Boolean {
        val distance = calculateDistance(lat1, lon1, lat2, lon2)
        return distance > thresholdKm
    }
} 