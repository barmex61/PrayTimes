package com.fatih.prayertime.domain.use_case.location_use_cases

import com.fatih.prayertime.domain.model.LocationPair
import com.fatih.prayertime.domain.repository.PrayDatabaseRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCurrentLocationUseCase @Inject constructor(private val prayDatabaseRepository: PrayDatabaseRepository) {
    operator fun invoke() : Flow<LocationPair> {
       return prayDatabaseRepository.getCurrentLocationPair()
    }
}