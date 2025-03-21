package com.fatih.prayertime.util.model.state

import com.fatih.prayertime.data.remote.dto.qurandto.SurahInfo
import com.fatih.prayertime.data.remote.dto.qurandto.QuranApiData
import com.fatih.prayertime.domain.model.JuzInfo
import com.fatih.prayertime.util.model.enums.PlaybackMode
import com.fatih.prayertime.util.utils.QuranUtils.getTransliterations


data class QuranScreenState(
    val surahList: List<SurahInfo> = emptyList(),
    val juzList: List<JuzInfo> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val selectedReciter: String = "",
    val selectedTranslation: String = "",
    val selectedTransliteration: String = "Turkish",
    val translationList : List<QuranApiData> = listOf(),
    val reciterList : List<QuranApiData> = listOf(),
    val transliterationList : Map<String, String> = getTransliterations(),
    val selectedTabIndex: Int = 0,

)
