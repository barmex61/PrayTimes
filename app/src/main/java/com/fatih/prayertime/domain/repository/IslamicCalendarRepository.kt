package com.fatih.prayertime.domain.repository

import com.fatih.prayertime.data.remote.dto.islamicdaysdto.IslamicDaysDataDTO
import com.fatih.prayertime.util.Resource

interface IslamicCalendarRepository {
    suspend fun getIslamicCalendarForMonth(
        month : Int,
        year : Int,
        calendarMethod : String
    ): Resource<List<IslamicDaysDataDTO>>
}