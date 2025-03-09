package com.fatih.prayertime.domain.repository

import com.fatih.prayertime.data.local.entity.PrayerStatisticsEntity
import kotlinx.coroutines.flow.Flow

interface PrayerStatisticsRepository {
    fun getAllStatistics(): Flow<List<PrayerStatisticsEntity>>
    fun getStatisticsByDate(date: String): Flow<List<PrayerStatisticsEntity>>
    suspend fun insertStatistic(statistic: PrayerStatisticsEntity)
    suspend fun updateStatistic(statistic: PrayerStatisticsEntity)
    fun getCompletedPrayersCount(): Flow<Int>
    fun getOnTimePrayersCount(): Flow<Int>
    fun getStatisticsBetweenDates(startDate: String, endDate: String): Flow<List<PrayerStatisticsEntity>>
} 