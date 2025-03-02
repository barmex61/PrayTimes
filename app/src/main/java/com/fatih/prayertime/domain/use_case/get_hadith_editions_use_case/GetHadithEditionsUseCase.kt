package com.fatih.prayertime.domain.use_case.get_hadith_editions_use_case

import com.fatih.prayertime.domain.repository.HadithRepository
import com.fatih.prayertime.util.Resource
import com.fatih.prayertime.util.Status
import java.lang.Exception
import javax.inject.Inject

class GetHadithEditionsUseCase @Inject constructor(private val hadithRepository: HadithRepository) {
    suspend operator fun invoke() = hadithRepository.getHadithEditions()
}