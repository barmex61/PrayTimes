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
    val currentAudioInfo: AudioInfo = AudioInfo()
)
data class AudioInfo(
    val surahName: String = "",
    val reciter: String = "",
    val reciterName: String = "",
    val bitrate: Int = 128,
    val playbackMode: PlaybackMode = PlaybackMode.VERSE_STREAM,
    val audioPath : String = "audio",
    var playbackSpeed: Float = 1f,
    val shouldCacheAudio : Boolean = true,
    val surahNumber: Int = 1,
    val ayahNumber: Int = 1,
    val maxAyahNumber : Int =0,
    val minAyahNumber : Int = 0
){
    val audioNumber : Int
        get() = when(playbackMode){
            PlaybackMode.SURAH -> surahNumber
            PlaybackMode.VERSE_STREAM -> ayahNumber
        }
}
