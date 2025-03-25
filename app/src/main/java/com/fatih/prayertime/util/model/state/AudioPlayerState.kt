package com.fatih.prayertime.util.model.state

import com.fatih.prayertime.util.model.enums.PlaybackMode

data class AudioPlayerState(
    val isPlaying: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,

    val duration: Float = 0f,
    val currentPosition: Float = 0f,

    val downloadProgress: Int = 0,
    val downloadedSize: Long = 0L,
    val totalSize: Long = 0L,

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
    val ayahNumber: Int = 1
){
    val audioNumber : Int
        get() = when(playbackMode){
            PlaybackMode.SURAH -> surahNumber
            PlaybackMode.VERSE_STREAM -> ayahNumber
        }
}
