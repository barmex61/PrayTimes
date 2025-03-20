package com.fatih.prayertime.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class AudioSettings(
    val shouldCacheAudio: Boolean = false,
    val selectedReciter: String = "",
    val selectedReciterIndex: Int = -1,
    val selectedTranslation: String = "",
    val selectedTransliteration: String = "",
    val playbackSpeed: Float = 1f,
    val autoHidePlayer: Boolean = false,
    val playByVerse: Boolean = false,
    val fontSize: Float = 1f
) 