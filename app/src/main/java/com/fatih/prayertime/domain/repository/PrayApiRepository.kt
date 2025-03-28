package com.fatih.prayertime.domain.repository

import com.fatih.prayertime.data.remote.dto.praytimesdto.MonthlyPrayTimesResponseDTO
import com.fatih.prayertime.util.model.state.Resource

interface PrayApiRepository {

    suspend fun getMonthlyPrayTimes(
        year: Int, 
        month: Int, 
        latitude: Double, 
        longitude: Double,
        method: Int? = null,
        adjustments: String? = null,
        tuneString: String? = null,
        school: Int = 0,
        midnightMode: Int = 0
    ): Resource<MonthlyPrayTimesResponseDTO>
}