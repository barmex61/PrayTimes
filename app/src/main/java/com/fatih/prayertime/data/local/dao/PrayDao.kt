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

}