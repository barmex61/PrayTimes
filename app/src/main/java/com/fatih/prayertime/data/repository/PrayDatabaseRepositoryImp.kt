package com.fatih.prayertime.data.repository

import com.fatih.prayertime.data.local.dao.PrayDao
import com.fatih.prayertime.domain.model.Address
import com.fatih.prayertime.domain.model.PrayTimes
import com.fatih.prayertime.domain.repository.PrayDatabaseRepository
import javax.inject.Inject

class PrayDatabaseRepositoryImp @Inject constructor(private val prayDao: PrayDao): PrayDatabaseRepository {
    override suspend fun insertPrayTime(prayTimes: PrayTimes) {
        prayDao.insertPrayTime(prayTimes)
    }

    override suspend fun getDailyPrayTimesAtAddress(
        country: String,
        district: String,
        city: String,
        date: String
    ): List<PrayTimes>? {
        return prayDao.getPrayTimesAtAddress(country, district, city, date)
    }

    override suspend fun getLastKnownAddress(): Address? {
        val prayTime = prayDao.getPrayTimes()
        return prayTime?.let {
            Address(
                country = it.country,
                city = it.city,
                district = it.district,
                street = it.street,
                latitude = it.latitude,
                longitude = it.longitude,
                fullAddress = it.fullAddress
            )
        }
    }
}