package com.fatih.prayertime.util.model.state

import com.fatih.prayertime.util.model.enums.PlaybackMode

data class AudioPlayerState(
    // Oynatma durumu
    val isPlaying: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,

    // Pozisyon ve süre
    val duration: Float = 0f,
    val currentPosition: Float = 0f,

    // İndirme durumu
    val downloadProgress: Int = 0,
    val downloadedSize: Long = 0L,
    val totalSize: Long = 0L,

    // Mevcut audio bilgileri
    val currentAudioInfo: AudioInfo? = null
)

data class AudioInfo(
    val surahName: String,
    val audioNumber: Int,
    val reciter: String,
    val reciterName: String,
    val bitrate: Int,
    val playbackMode: PlaybackMode,
    val playbackSpeed: Float
)
