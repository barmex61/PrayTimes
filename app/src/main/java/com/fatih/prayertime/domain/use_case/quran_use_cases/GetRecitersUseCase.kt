package com.fatih.prayertime.domain.use_case.quran_use_cases

import com.fatih.prayertime.domain.repository.QuranApiRepository
import javax.inject.Inject

class GetRecitersUseCase @Inject constructor(private val quranApiRepository: QuranApiRepository) {
    suspend fun getVerseByVerseReciters() = quranApiRepository.getVerseByVerseReciters()
    suspend fun getSurahBySurahReciters() = quranApiRepository.getSurahBySurahReciters()
}