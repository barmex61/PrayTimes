package com.fatih.prayertime.util.model.state

import com.fatih.prayertime.data.remote.dto.qurandto.QuranApiData
import com.fatih.prayertime.util.model.enums.PlaybackMode
import com.fatih.prayertime.util.utils.QuranUtils.getTransliterations

data class QuranSettingsState(
    val selectedReciter: String = "",
    val selectedReciterIndex: Int = 0,
    val selectedTranslation: String = "",
    val selectedTransliteration: String = "Turkish",
    val translationList : List<QuranApiData> = listOf(),
    val reciterList : List<QuranApiData> = listOf(),
    val transliterationList : Map<String, String> = getTransliterations(),
    val autoHidePlayer: Boolean = true,
    val autoScrollAyah : Boolean = true,
    val playAyahWithDoubleClick : Boolean = true,
    val playbackSpeed: Float = 1f,
    val playByVerse: Boolean = true,
    val shouldCacheAudio: Boolean = true,
    val playbackMode: PlaybackMode = PlaybackMode.VERSE_STREAM,
    val isPreloadingVerses: Boolean = false,
    val showSettings : Boolean = false,
    val showCacheInfo : Boolean = false,
    val isError : String? = null,
    val isLoading : Boolean = false,
    val fontSize: Float = 1f
)
