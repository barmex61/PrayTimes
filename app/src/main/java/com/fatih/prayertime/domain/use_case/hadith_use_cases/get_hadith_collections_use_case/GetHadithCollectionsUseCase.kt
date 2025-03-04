package com.fatih.prayertime.domain.use_case.hadith_use_cases.get_hadith_collections_use_case

import com.fatih.prayertime.domain.repository.HadithRepository
import javax.inject.Inject

class GetHadithCollectionsUseCase @Inject constructor(private val hadithRepository: HadithRepository) {
    suspend operator fun invoke(collectionPath : String) = hadithRepository.getHadithCollections(collectionPath)
}