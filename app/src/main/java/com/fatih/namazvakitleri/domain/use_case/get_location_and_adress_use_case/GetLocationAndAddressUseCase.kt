package com.fatih.namazvakitleri.domain.use_case.get_location_and_adress_use_case

import com.fatih.namazvakitleri.domain.model.Address
import com.fatih.namazvakitleri.domain.repository.LocationAndAddressRepository
import com.fatih.namazvakitleri.util.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetLocationAndAddressUseCase @Inject constructor(private val locationAndAddressRepository: LocationAndAddressRepository){
    suspend operator fun invoke() : Flow<Resource<Address>> = locationAndAddressRepository.getLocationAndAddressInformation()
}