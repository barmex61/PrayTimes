package com.fatih.prayertime.domain.use_case.location_use_cases.get_last_known_address_from_database_use_case

import com.fatih.prayertime.domain.model.Address
import com.fatih.prayertime.domain.repository.PrayDatabaseRepository
import javax.inject.Inject

class GetLastKnowAddressFromDatabaseUseCase @Inject constructor(private val prayDatabaseRepository: PrayDatabaseRepository){
    suspend operator fun invoke() : Address? = prayDatabaseRepository.getLastKnownAddress()
}