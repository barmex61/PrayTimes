package com.fatih.prayertime.domain.use_case.pray_times_use_cases

import com.fatih.prayertime.domain.model.Address
import com.fatih.prayertime.domain.repository.PrayDatabaseRepository
import javax.inject.Inject

class GetDailyPrayTimesWithAddressAndDateUseCase @Inject constructor(private val prayDatabaseRepository: PrayDatabaseRepository) {
    suspend operator fun invoke(address: Address,date : String) = prayDatabaseRepository.getDailyPrayTimesWithAddressAndDate(
        address,
        date
    )


}