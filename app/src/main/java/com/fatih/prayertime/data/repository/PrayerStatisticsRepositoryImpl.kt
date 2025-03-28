package com.fatih.prayertime.data.repository

import com.fatih.prayertime.data.local.dao.PrayerStatisticsDao
import com.fatih.prayertime.data.local.entity.PrayerStatisticsEntity
import com.fatih.prayertime.domain.repository.PrayerStatisticsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

class PrayerStatisticsRepositoryImpl @Inject constructor(
    private val statisticsDao: PrayerStatisticsDao
) : PrayerStatisticsRepository {
    override fun getAllStatistics(): Flow<List<PrayerStatisticsEntity>> {
        return statisticsDao.getAllStatistics()
    }

    override fun getStatisticsByDate(date: String): Flow<List<PrayerStatisticsEntity>> {
        return statisticsDao.getStatisticsByDate(date)
    }

    override suspend fun insertStatistic(statistic: PrayerStatisticsEntity) {
        statisticsDao.insertStatistic(statistic)
    }

    override suspend fun updateStatistic(statistic: PrayerStatisticsEntity) {
        statisticsDao.updateStatistic(statistic)
    }

    override fun getCompletedPrayersCount(): Flow<Int> {
        return statisticsDao.getCompletedPrayersCount()
    }
    override fun getStatisticsBetweenDates(startDate: Long, endDate: Long): Flow<List<PrayerStatisticsEntity>> {
        return statisticsDao.getStatisticsBetweenDates(startDate, endDate)
    }

    override suspend fun isExist(prayerType: String, date: String): Boolean {
        return statisticsDao.exists(prayerType, date)
    }
} 