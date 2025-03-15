package com.fatih.prayertime.domain.use_case.statistics_use_cases

import com.fatih.prayertime.data.local.entity.PrayerStatisticsEntity
import com.fatih.prayertime.domain.repository.PrayerStatisticsRepository
import com.fatih.prayertime.domain.use_case.formatted_use_cases.FormattedUseCase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetStatisticsUseCase @Inject constructor(
    private val repository: PrayerStatisticsRepository
) {
    suspend operator fun invoke(startDate: Long, endDate: Long): Flow<List<PrayerStatisticsEntity>> {
        return repository.getStatisticsBetweenDates(startDate, endDate)
    }
} 