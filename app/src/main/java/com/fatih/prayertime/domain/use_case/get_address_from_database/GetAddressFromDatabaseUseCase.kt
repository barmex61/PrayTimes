package com.fatih.prayertime.domain.use_case.get_address_from_database

import com.fatih.prayertime.domain.repository.LocationAndAddressRepository
import javax.inject.Inject

class GetAddressFromDatabaseUseCase @Inject constructor(
    private val locationAndAddressRepository: LocationAndAddressRepository
) {
    suspend operator fun invoke() = locationAndAddressRepository.getCurrentAddress()
}