package com.fatih.prayertime.domain.use_case.settings_use_cases

import com.fatih.prayertime.domain.repository.SharedPrefRepository
import javax.inject.Inject

class GetStatisticSharedPrefUseCase @Inject constructor(private val sharedPrefRepository: SharedPrefRepository) {
    operator fun invoke() = sharedPrefRepository.getStatisticKey()
}