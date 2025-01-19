package com.fatih.prayertime.domain.use_case.pray_times_use_cases.get_pray_times_at_address_from_database_use_case

import com.fatih.prayertime.domain.model.Address
import com.fatih.prayertime.domain.repository.PrayDatabaseRepository
import javax.inject.Inject

class GetDailyPrayTimesWithAddressAndDateUseCase @Inject constructor(private val prayDatabaseRepository: PrayDatabaseRepository) {
    suspend operator fun invoke(address: Address,date : String) = prayDatabaseRepository.getDailyPrayTimesWithAddressAndDate(
        address.country?:"",
        address.district?:"",
        address.city?:"",
        date
    )


}