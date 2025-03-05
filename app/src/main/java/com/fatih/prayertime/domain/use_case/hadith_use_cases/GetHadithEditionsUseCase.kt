package com.fatih.prayertime.domain.use_case.hadith_use_cases

import com.fatih.prayertime.domain.repository.HadithRepository
import javax.inject.Inject

class GetHadithEditionsUseCase @Inject constructor(private val hadithRepository: HadithRepository) {
    suspend operator fun invoke() = hadithRepository.getHadithEditions()
}