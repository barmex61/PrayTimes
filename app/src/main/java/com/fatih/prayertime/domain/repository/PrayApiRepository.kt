package com.fatih.prayertime.domain.repository

import com.fatih.prayertime.data.remote.dto.praytimesdto.MonthlyPrayTimesResponseDTO
import com.fatih.prayertime.util.model.state.Resource

interface PrayApiRepository {

    suspend fun getMonthlyPrayTimes(year : Int, month : Int, latitude : Double, longitude : Double) : Resource<MonthlyPrayTimesResponseDTO>
}