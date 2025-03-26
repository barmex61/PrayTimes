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

    @Query("SELECT * FROM prayer_statistics WHERE dateLong >= :startDate AND dateLong <= :endDate ORDER BY dateLong ASC")
    fun getStatisticsBetweenDates(startDate: Long, endDate: Long): Flow<List<PrayerStatisticsEntity>>

    @Query("SELECT COUNT(*) > 0 FROM prayer_statistics WHERE prayerType = :prayerType AND date = :date")
    suspend fun exists(prayerType: String, date: String): Boolean
} 