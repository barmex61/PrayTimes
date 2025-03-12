package com.fatih.prayertime.domain.repository

import com.fatih.prayertime.data.local.entity.PrayerStatisticsEntity
import kotlinx.coroutines.flow.Flow

interface PrayerStatisticsRepository {
    fun getAllStatistics(): Flow<List<PrayerStatisticsEntity>>
    fun getStatisticsByDate(date: String): Flow<List<PrayerStatisticsEntity>>
    suspend fun insertStatistic(statistic: PrayerStatisticsEntity)
    suspend fun updateStatistic(statistic: PrayerStatisticsEntity)
    fun getCompletedPrayersCount(): Flow<Int>
    fun getStatisticsBetweenDates(startDate: Long, endDate: Long): Flow<List<PrayerStatisticsEntity>>
    suspend fun isExist(prayerType: String, date: String): Boolean
} 