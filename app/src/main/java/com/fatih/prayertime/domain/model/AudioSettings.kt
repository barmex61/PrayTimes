package com.fatih.prayertime.domain.model

import com.fatih.prayertime.util.model.enums.PlaybackMode
import kotlinx.serialization.Serializable

@Serializable
data class AudioSettings(
    val shouldCacheAudio: Boolean = false,
    val selectedReciter: String = "",
    val selectedReciterIndex: Int = -1,
    val selectedTranslation: String = "",
    val selectedTransliteration: String = "",
    val playbackSpeed: Float = 1f,
    val autoHidePlayer: Boolean = true,
    val autoScrollAyah : Boolean = true,
    val playAyahWithDoubleClick : Boolean = true,
    val playbackMode : PlaybackMode = PlaybackMode.VERSE_STREAM,
    val fontSize: Float = 1f
) 