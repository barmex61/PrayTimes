package com.fatih.prayertime.domain.use_case.statistics_use_cases

import com.fatih.prayertime.data.local.entity.PrayerStatisticsEntity
import com.fatih.prayertime.domain.repository.PrayerStatisticsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllStatisticsUseCase @Inject constructor(
    private val repository: PrayerStatisticsRepository
) {
    operator fun invoke(): Flow<List<PrayerStatisticsEntity>> {
        return repository.getAllStatistics()
    }
} 