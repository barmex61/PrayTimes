package com.fatih.prayertime.util.model.state

import com.fatih.prayertime.data.remote.dto.qurandto.SurahInfo
import com.fatih.prayertime.domain.model.JuzInfo


data class QuranScreenState(
    val surahList: List<SurahInfo> = emptyList(),
    val juzList: List<JuzInfo> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val isFabExpanded: Boolean = false,
    val selectedReciter: String = "Mishari Rashid al-`Afasy",
    val selectedTranslation: String = "Türkçe",
    val selectedPronunciation: String = "Türkçe",
    val selectedTabIndex: Int = 0
)
