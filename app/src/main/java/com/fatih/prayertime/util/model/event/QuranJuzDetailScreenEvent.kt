package com.fatih.prayertime.util.model.event

import com.fatih.prayertime.util.model.enums.PlaybackMode

sealed class QuranJuzDetailScreenEvent(){
    object ToggleAutoHidePlayer : QuranJuzDetailScreenEvent()
    data class SetPlaybackSpeed(val speed: Float) : QuranJuzDetailScreenEvent()
    object TogglePlaybackMode : QuranJuzDetailScreenEvent()
    data class SetShouldCacheAudio(val shouldCache: Boolean) : QuranJuzDetailScreenEvent()
    data class SetPlaybackMode(val mode: PlaybackMode) : QuranJuzDetailScreenEvent()
    data object StartVersePreload : QuranJuzDetailScreenEvent()
    object ToggleSettingsSheet : QuranJuzDetailScreenEvent()
    object ToggleCacheInfoDialog : QuranJuzDetailScreenEvent()
    data class SetTranslation(val translation: String) : QuranJuzDetailScreenEvent()
    data class SetReciter(val reciter: String, val reciterIndex: Int) : QuranJuzDetailScreenEvent()
    data class SetTransliteration(val transliteration: String) : QuranJuzDetailScreenEvent()
    data class SelectSurah(val surahNumber: Int) : QuranJuzDetailScreenEvent()
} 