package com.fatih.prayertime.domain.use_case.statistics_use_cases

import com.fatih.prayertime.data.local.entity.PrayerStatisticsEntity
import com.fatih.prayertime.domain.repository.PrayerStatisticsRepository
import javax.inject.Inject

class InsertPlayerStatisticsUseCase @Inject constructor(
    private val repository: PrayerStatisticsRepository
) {
    suspend operator fun invoke(statistic: PrayerStatisticsEntity) {
        repository.insertStatistic(statistic)
    }
} 