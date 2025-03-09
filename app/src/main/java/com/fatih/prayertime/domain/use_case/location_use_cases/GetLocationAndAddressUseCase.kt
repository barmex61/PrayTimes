package com.fatih.prayertime.domain.use_case.location_use_cases

import com.fatih.prayertime.domain.model.Address
import com.fatih.prayertime.domain.repository.LocationAndAddressRepository
import com.fatih.prayertime.util.model.state.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetLocationAndAddressUseCase @Inject constructor(private val locationAndAddressRepository: LocationAndAddressRepository){
    suspend operator fun invoke() : Flow<Resource<Address>> = locationAndAddressRepository.getLocationAndAddressInformation()
}