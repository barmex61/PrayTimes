package com.fatih.prayertime.domain.use_case.pray_times_use_cases

import com.fatih.prayertime.domain.repository.PrayDatabaseRepository
import javax.inject.Inject

class DeletePrayTimesBeforeDateUseCase @Inject constructor(private val prayDatabaseRepository: PrayDatabaseRepository) {
    suspend operator fun invoke(dateLong : Long) = prayDatabaseRepository.deletePrayTimesBeforeDate(dateLong)
}