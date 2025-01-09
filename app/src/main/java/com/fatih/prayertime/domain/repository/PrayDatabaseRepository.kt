package com.fatih.prayertime.domain.repository

import com.fatih.prayertime.domain.model.Address
import com.fatih.prayertime.domain.model.PrayTimes

interface PrayDatabaseRepository {

    suspend fun getLastKnownAddress() : Address?

    suspend fun insertPrayTime(prayTimes: PrayTimes)

    suspend fun getDailyPrayTimesAtAddress(
        country: String,
        district: String,
        city: String,
        date: String
    ) : List<PrayTimes>?

}