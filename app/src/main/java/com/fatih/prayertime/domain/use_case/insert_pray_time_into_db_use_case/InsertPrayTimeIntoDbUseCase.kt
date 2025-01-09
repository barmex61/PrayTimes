package com.fatih.prayertime.domain.use_case.insert_pray_time_into_db_use_case

import com.fatih.prayertime.domain.model.PrayTimes
import com.fatih.prayertime.domain.repository.PrayDatabaseRepository
import javax.inject.Inject

class InsertPrayTimeIntoDbUseCase @Inject constructor(private val prayDatabaseRepository: PrayDatabaseRepository) {
    suspend operator fun invoke(prayTimes: PrayTimes) = prayDatabaseRepository.insertPrayTime(prayTimes)
}