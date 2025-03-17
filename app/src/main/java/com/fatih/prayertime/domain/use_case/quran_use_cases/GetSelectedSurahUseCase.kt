package com.fatih.prayertime.domain.use_case.quran_use_cases

import com.fatih.prayertime.domain.repository.QuranApiRepository
import javax.inject.Inject

class GetSelectedSurahUseCase @Inject constructor(private val quranApiRepository: QuranApiRepository) {
    suspend operator fun invoke(surahNumber: Int,surahPath : String) = quranApiRepository.getSelectedSurah(surahNumber,surahPath)
}