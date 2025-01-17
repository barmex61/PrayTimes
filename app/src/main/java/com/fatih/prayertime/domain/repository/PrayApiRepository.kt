package com.fatih.prayertime.domain.repository

import com.fatih.prayertime.util.Resource
import com.fatih.prayertime.data.remote.dto.DailyPrayResponseDTO
import com.fatih.prayertime.data.remote.dto.MonthlyPrayTimesResponseDTO

interface PrayApiRepository {

    suspend fun getMonthlyPrayTimes(year : Int, month : Int, latitude : Double, longitude : Double) : Resource<MonthlyPrayTimesResponseDTO>
}