package com.fatih.prayertime.domain.use_case.islamic_calendar_use_case

import com.fatih.prayertime.domain.repository.IslamicCalendarRepository
import javax.inject.Inject

class GetIslamicCalendarForMonthUseCase @Inject constructor(private val islamicCalendarRepository: IslamicCalendarRepository) {

    suspend operator fun invoke(month: Int, year: Int, calendarMethod: String) = islamicCalendarRepository.getIslamicCalendarForMonth(month, year, calendarMethod)
}