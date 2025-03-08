package com.fatih.prayertime.data.local.dao

import androidx.room.*
import com.fatih.prayertime.data.local.entity.PrayerStatisticsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PrayerStatisticsDao {
    @Query("SELECT * FROM prayer_statistics ORDER BY date DESC")
    fun getAllStatistics(): Flow<List<PrayerStatisticsEntity>>

    @Query("SELECT * FROM prayer_statistics WHERE date = :date")
    fun getStatisticsByDate(date: String): Flow<List<PrayerStatisticsEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStatistic(statistic: PrayerStatisticsEntity)

    @Update
    suspend fun updateStatistic(statistic: PrayerStatisticsEntity)

    @Query("SELECT COUNT(*) FROM prayer_statistics WHERE isCompleted = 1")
    fun getCompletedPrayersCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM prayer_statistics WHERE isCompleted = 1 AND isOnTime = 1")
    fun getOnTimePrayersCount(): Flow<Int>

    @Query("SELECT * FROM prayer_statistics WHERE date BETWEEN :startDate AND :endDate")
    fun getStatisticsBetweenDates(startDate: String, endDate: String): Flow<List<PrayerStatisticsEntity>>
} 