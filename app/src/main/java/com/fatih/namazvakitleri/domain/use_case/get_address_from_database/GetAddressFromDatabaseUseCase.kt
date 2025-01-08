package com.fatih.namazvakitleri.domain.use_case.get_address_from_database

import com.fatih.namazvakitleri.data.local.dao.AddressDao
import com.fatih.namazvakitleri.domain.repository.LocationAndAddressRepository
import com.fatih.namazvakitleri.domain.use_case.get_location_and_adress_use_case.GetLocationAndAddressUseCase
import javax.inject.Inject

class GetAddressFromDatabaseUseCase @Inject constructor(
    private val locationAndAddressRepository: LocationAndAddressRepository
) {
    suspend operator fun invoke() = locationAndAddressRepository.getCurrentAddress()
}