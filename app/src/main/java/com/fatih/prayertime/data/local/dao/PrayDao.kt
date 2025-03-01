package com.fatih.prayertime.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.fatih.prayertime.domain.model.LocationPair
import com.fatih.prayertime.domain.model.PrayTimes
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

@Dao
interface PrayDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPrayTime(prayTime: PrayTimes)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllPrayTimes(prayTimes: List<PrayTimes>)

    @Query("""
    SELECT * FROM PrayTimes
    WHERE (country = :country OR (country IS NULL AND :country IS NULL))
    AND (city = :city OR (city IS NULL AND :city IS NULL))
    AND (district = :district OR (district IS NULL AND :district IS NULL))
    AND date = :date
""")
    fun getPrayTimesWithAddressAndDate(country: String?, district: String?, city: String?, date: String):PrayTimes?

    @Query("SELECT * FROM PrayTimes ORDER BY createdAt DESC LIMIT 1")
    suspend fun getLastInsertedPrayTime() : PrayTimes?

    @Query("SELECT latitude, longitude FROM PrayTimes ORDER BY createdAt DESC LIMIT 1")
    fun getCurrentLocationPair() : Flow<LocationPair>

    @Query("DELETE FROM PrayTimes WHERE dateLong < :dateLong")
    suspend fun deletePrayTimesBeforeTheDate(dateLong: Long)
}