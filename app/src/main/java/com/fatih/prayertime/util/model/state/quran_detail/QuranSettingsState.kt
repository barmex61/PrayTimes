package com.fatih.prayertime.util.model.state.quran_detail

import com.fatih.prayertime.data.remote.dto.qurandto.QuranApiData
import com.fatih.prayertime.util.model.enums.PlaybackMode
import com.fatih.prayertime.util.utils.QuranUtils.getTransliterations

data class QuranSettingsState(
    val selectedReciter: String = "",
    val selectedReciterIndex: Int = 0,
    val selectedTranslation: String = "",
    val selectedTransliteration: String = "Turkish",

    val translationList: List<QuranApiData> = listOf(),
    val reciterList: List<QuranApiData> = listOf(),
    val transliterationList: Map<String, String> = getTransliterations(),

    val playbackMode: PlaybackMode = PlaybackMode.VERSE_STREAM,
    val playbackSpeed: Float = 1f,
    val shouldCacheAudio: Boolean = true,

    val autoHidePlayer: Boolean = true,
    val autoScrollAyah: Boolean = true,
    val playAyahWithDoubleClick: Boolean = true,
    val fontSize: Float = 1f,

    val isLoading : Boolean = false,
    val isError : String? = null,

    val showSettings: Boolean = false,
    val showCacheInfo: Boolean = false,
)
