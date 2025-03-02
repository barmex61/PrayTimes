package com.fatih.prayertime.domain.use_case.islamic_calendar_use_case

import com.fatih.prayertime.domain.model.IslamicDaysData
import com.fatih.prayertime.domain.repository.IslamicCalendarRepository
import com.fatih.prayertime.util.Resource
import com.fatih.prayertime.util.Status
import com.fatih.prayertime.util.toIslamicDaysData
import javax.inject.Inject

class GetIslamicCalendarForMonthUseCase @Inject constructor(private val islamicCalendarRepository: IslamicCalendarRepository) {

    suspend operator fun invoke(month: Int, year: Int, calendarMethod: String) : Resource<List<IslamicDaysData>> {
        return try {
            val response = islamicCalendarRepository.getIslamicCalendarForMonth(month, year, calendarMethod)
            if (response.status == Status.SUCCESS) Resource.success(response.data!!.toIslamicDaysData())
            else Resource.error(message = response.message)
        }catch (e:Exception){
            Resource.error(message = e.message)
        }

    }
}