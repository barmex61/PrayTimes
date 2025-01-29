package com.fatih.prayertime.domain.repository

import com.fatih.prayertime.domain.model.Address
import com.fatih.prayertime.domain.model.LocationPair
import com.fatih.prayertime.domain.model.PrayTimes
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface PrayDatabaseRepository {

    suspend fun getLastKnownAddress() : Address?

    suspend fun insertPrayTime(prayTimes: PrayTimes)

    suspend fun insertAllPrayTimes(prayTimes: List<PrayTimes>)

    suspend fun getDailyPrayTimesWithAddressAndDate(
        address: Address,
        date: String
    ) : Flow<PrayTimes?>

    suspend fun getCurrentLocationPair() : Flow<LocationPair>

}