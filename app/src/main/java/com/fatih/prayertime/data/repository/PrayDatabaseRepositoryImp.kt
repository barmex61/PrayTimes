package com.fatih.prayertime.data.repository

import com.fatih.prayertime.data.local.dao.PrayDao
import com.fatih.prayertime.domain.model.Address
import com.fatih.prayertime.domain.model.PrayTimes
import com.fatih.prayertime.domain.repository.PrayDatabaseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class PrayDatabaseRepositoryImp @Inject constructor(private val prayDao: PrayDao): PrayDatabaseRepository {

    override suspend fun insertPrayTime(prayTimes: PrayTimes) {
        prayDao.insertPrayTime(prayTimes)
    }

    override suspend fun insertAllPrayTimes(prayTimes: List<PrayTimes>) {
        prayDao.insertAllPrayTimes(prayTimes)
    }

    override suspend fun getDailyPrayTimesWithAddressAndDate(
        address: Address,
        date: String
    ): Flow<PrayTimes?> {
        val country = address.country
        val district = address.district
        val city = address.city
        val prayTime = prayDao.getPrayTimesWithAddressAndDate(country, district, city, date)
        return prayTime
    }

    override suspend fun getLastKnownAddress(): Address? {
        val prayTime = prayDao.getLastInsertedPrayTime()
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