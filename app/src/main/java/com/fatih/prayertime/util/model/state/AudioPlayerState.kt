package com.fatih.prayertime.util.model.state

data class AudioPlayerState(
    val audioLoading: Boolean = false,
    val audioError: String? = null,
    val audioPlaying: Boolean = false,
    val audioDuration: Float = 0f,
    val currentAudioPosition: Float = 0f,
    val downloadProgress: Int = 0,
    val downloadedSize: Long = 0L,
    val totalSize: Long = 0L
)
