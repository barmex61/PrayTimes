package com.fatih.prayertime.domain.use_case.statistics_use_cases

import com.fatih.prayertime.domain.repository.PrayerStatisticsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetPrayerCountsUseCase @Inject constructor(
    private val repository: PrayerStatisticsRepository
) {
    fun getCompletedCount(): Flow<Int> {
        return repository.getCompletedPrayersCount()
    }

    fun getOnTimeCount(): Flow<Int> {
        return repository.getOnTimePrayersCount()
    }
} 