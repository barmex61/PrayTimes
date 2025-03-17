package com.fatih.prayertime.util.model.state

import com.fatih.prayertime.data.remote.dto.qurandto.SurahInfo
import com.fatih.prayertime.data.remote.dto.qurandto.QuranApiData
import com.fatih.prayertime.domain.model.JuzInfo
import com.fatih.prayertime.util.utils.QuranUtils.getTransliterations


data class QuranScreenState(
    val surahList: List<SurahInfo> = emptyList(),
    val selectedSurah : SurahInfo? = null,
    val selectedSurahNumber : Int = 1,
    var currentAyahNumber : Int = 1,
    val juzList: List<JuzInfo> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val selectedReciter: String = "",
    val selectedTranslation: String = "",
    val translationList : List<QuranApiData> = listOf(),
    val reciterList : List<QuranApiData> = listOf(),
    val transliterationList : Map<String, String> = getTransliterations(),
    val selectedTransliteration: String = "Turkish",
    val selectedTabIndex: Int = 0,
    val selectedJuz: JuzInfo? = null,
    val isAudioPlaying: Boolean = false,
    val currentAudioPosition: Float = 0f,
    val audioDuration: Float = 0f,
    val isAudioLoading: Boolean = false,
    val audioError: String? = null
)
