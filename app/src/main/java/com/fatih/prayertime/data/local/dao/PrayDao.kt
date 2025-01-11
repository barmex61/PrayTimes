package com.fatih.prayertime.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.fatih.prayertime.domain.model.PrayTimes

@Dao
interface PrayDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPrayTime(prayTime: PrayTimes)

    @Query("SELECT * FROM PrayTimes WHERE date = :date")
    suspend fun getPrayTime(date: String): PrayTimes?

    @Query("SELECT * FROM PrayTimes WHERE country = :country AND district = :district AND city = :city AND date = :date ORDER BY createdAt DESC")
    suspend fun getPrayTimesAtAddress(country: String, district: String, city: String, date: String): List<PrayTimes>?

    @Query("SELECT * FROM PrayTimes ORDER BY createdAt DESC LIMIT 1")
    suspend fun getPrayTimes() : PrayTimes?
}