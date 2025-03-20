package com.fatih.prayertime.util.model.state

import com.fatih.prayertime.data.remote.dto.qurandto.SurahInfo
import com.fatih.prayertime.domain.model.JuzInfo

data class QuranJuzDetailScreenState(
    val isLoading: Boolean = false,
    val isError: String? = null,
    val selectedJuz: JuzInfo? = null,
    val selectedJuzNumber: Int = 1,
    val surahsInJuz: List<SurahInfo> = emptyList(),
    val selectedSurah: SurahInfo? = null,
    val selectedAyahNumber: Int = 1,
    val selectedSurahNumber: Int = 1
) 