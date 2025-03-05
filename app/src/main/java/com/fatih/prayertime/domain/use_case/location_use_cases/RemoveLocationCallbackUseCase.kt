package com.fatih.prayertime.domain.use_case.location_use_cases

import com.fatih.prayertime.domain.repository.LocationAndAddressRepository
import javax.inject.Inject

class RemoveLocationCallbackUseCase @Inject constructor(private val locationAndAddressRepository: LocationAndAddressRepository) {

    operator fun invoke() = locationAndAddressRepository.removeCallback()
}