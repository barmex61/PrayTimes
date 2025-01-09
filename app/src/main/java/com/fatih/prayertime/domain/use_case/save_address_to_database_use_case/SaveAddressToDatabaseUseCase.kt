package com.fatih.prayertime.domain.use_case.save_address_to_database_use_case

import com.fatih.prayertime.domain.model.Address
import com.fatih.prayertime.domain.repository.LocationAndAddressRepository
import javax.inject.Inject

class SaveAddressToDatabaseUseCase @Inject constructor(private val locationAndAddressRepository: LocationAndAddressRepository) {

    suspend operator fun invoke(address: Address) = locationAndAddressRepository.saveAddressToDatabase(address)
}