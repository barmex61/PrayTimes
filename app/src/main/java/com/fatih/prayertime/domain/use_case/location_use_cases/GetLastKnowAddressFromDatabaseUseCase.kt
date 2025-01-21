package com.fatih.prayertime.domain.use_case.location_use_cases

import com.fatih.prayertime.domain.model.Address
import com.fatih.prayertime.domain.repository.PrayDatabaseRepository
import javax.inject.Inject

class GetLastKnowAddressFromDatabaseUseCase @Inject constructor(private val prayDatabaseRepository: PrayDatabaseRepository){
    suspend operator fun invoke() : Address? = prayDatabaseRepository.getLastKnownAddress()
}