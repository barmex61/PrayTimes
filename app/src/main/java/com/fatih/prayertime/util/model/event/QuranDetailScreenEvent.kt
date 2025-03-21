package com.fatih.prayertime.util.model.event

import com.fatih.prayertime.util.model.enums.PlaybackMode

sealed class QuranDetailScreenEvent(){
    object ToggleAutoHidePlayer : QuranDetailScreenEvent()
    data class SetPlaybackSpeed(val speed: Float) : QuranDetailScreenEvent()
    data class SetShouldCacheAudio(val shouldCache: Boolean) : QuranDetailScreenEvent()
    object TogglePlaybackMode : QuranDetailScreenEvent()
    data object StartVersePreload : QuranDetailScreenEvent()
    object ToggleSettingsSheet : QuranDetailScreenEvent()
    object ToggleCacheInfoDialog : QuranDetailScreenEvent()
    data class SetTranslation(val translation: String) : QuranDetailScreenEvent()
    data class SetReciter(val reciter: String,val reciterIndex : Int) : QuranDetailScreenEvent()
    data class SetTransliteration(val transliteration: String) : QuranDetailScreenEvent()
    data class SetFontSize(val size: Float) : QuranDetailScreenEvent()
    object ToggleAutoScrollAyah : QuranDetailScreenEvent()
    object PlayAyahWithDoubleClick : QuranDetailScreenEvent()

}

