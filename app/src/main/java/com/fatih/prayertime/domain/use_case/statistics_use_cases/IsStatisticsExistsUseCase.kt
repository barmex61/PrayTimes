package com.fatih.prayertime.domain.use_case.statistics_use_cases

import com.fatih.prayertime.domain.repository.PrayerStatisticsRepository
import javax.inject.Inject

class IsStatisticsExistsUseCase @Inject constructor(private val playerStatisticsRepository: PrayerStatisticsRepository)  {
    suspend operator fun invoke(prayerType: String,date : String) = playerStatisticsRepository.isExist(prayerType, date)
}