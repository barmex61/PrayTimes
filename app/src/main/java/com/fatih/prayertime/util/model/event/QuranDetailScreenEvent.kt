package com.fatih.prayertime.util.model.event

import com.fatih.prayertime.util.model.enums.PlaybackMode

sealed class QuranDetailScreenEvent(){
    object ToggleAutoHidePlayer : QuranDetailScreenEvent()
    data class SetPlaybackSpeed(val speed: Float) : QuranDetailScreenEvent()
    object TogglePlaybackMode : QuranDetailScreenEvent()
    object ToggleSettingsSheet : QuranDetailScreenEvent()
    data class SetShouldCacheAudio(val shouldCache: Boolean) : QuranDetailScreenEvent()
    data object ToggleCacheInfoDialog : QuranDetailScreenEvent()
    data class SetPlaybackMode(val mode: PlaybackMode) : QuranDetailScreenEvent()
    data object StartVersePreload : QuranDetailScreenEvent()
}
