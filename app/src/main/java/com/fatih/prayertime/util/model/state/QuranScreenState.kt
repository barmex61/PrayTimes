package com.fatih.prayertime.util.model.state

import com.fatih.prayertime.data.remote.dto.qurandto.SurahInfo
import com.fatih.prayertime.data.remote.dto.qurandto.QuranApiData
import com.fatih.prayertime.domain.model.JuzInfo
import com.fatih.prayertime.util.utils.QuranUtils.getPronunciations


data class QuranScreenState(
    val surahList: List<SurahInfo> = emptyList(),
    val selectedSurah : SurahInfo? = null,
    val selectedSurahNumber : Int = 1,
    val juzList: List<JuzInfo> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val isFabExpanded: Boolean = false,
    val selectedReciter: String = "",
    val selectedTranslation: String = "",
    val selectedPronunciation: String = getPronunciations().first(),
    val translationList : List<QuranApiData> = listOf(),
    val reciterList : List<QuranApiData> = listOf(),
    val pronunciationList : List<String> = getPronunciations(),
    val selectedTabIndex: Int = 0
)
