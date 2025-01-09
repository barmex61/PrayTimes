package com.fatih.prayertime.domain.repository

import com.fatih.prayertime.util.Resource
import com.fatih.prayertime.data.remote.dto.DailyPrayResponseDTO

interface PrayApiRepository {

    suspend fun getPrayTimes(date : String,latitude : Double, longitude : Double) : Resource<DailyPrayResponseDTO>
}