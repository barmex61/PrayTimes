package com.fatih.prayertime.domain.use_case.pray_times_use_cases

import com.fatih.prayertime.domain.model.Address
import com.fatih.prayertime.domain.model.PrayTimes
import com.fatih.prayertime.domain.repository.PrayDatabaseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class GetDailyPrayTimesWithAddressAndDateUseCase @Inject constructor(private val prayDatabaseRepository: PrayDatabaseRepository) {
    suspend operator fun invoke(address: Address,date : String): Flow<PrayTimes?> = prayDatabaseRepository.getDailyPrayTimesWithAddressAndDate(
        address,
        date
    )


}