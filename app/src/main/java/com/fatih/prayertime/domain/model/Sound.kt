package com.fatih.prayertime.domain.model

data class Sound(
    val id: String,
    val displayName: String,
    val uri: String,
    val isSelected: Boolean = false
)

sealed class PlaybackState {
    object Initial : PlaybackState()
    data class Playing(val soundId: String) : PlaybackState()
    object Stopped : PlaybackState()
    data class Error(val message: String) : PlaybackState()
} 