package com.fatih.prayertime.domain.use_case.get_pray_times_at_address_from_database_use_case

import com.fatih.prayertime.domain.model.Address
import com.fatih.prayertime.domain.repository.PrayDatabaseRepository
import javax.inject.Inject

class GetDailyPrayTimesAtAddressFromDatabaseUseCase @Inject constructor(private val prayDatabaseRepository: PrayDatabaseRepository) {
    suspend operator fun invoke(address: Address,date : String) = prayDatabaseRepository.getDailyPrayTimesAtAddress(
        address.country?:"",
        address.district?:"",
        address.city?:"",
        date
    )


}