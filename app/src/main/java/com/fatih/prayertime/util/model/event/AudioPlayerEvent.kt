package com.fatih.prayertime.util.model.event

sealed class AudioPlayerEvent() {
    object PlayNextAudio : AudioPlayerEvent()
    object PlayPreviousAudio : AudioPlayerEvent()
    data class PlayExactAudioNumber(val audioNumber : Int) : AudioPlayerEvent()
    object PauseAudio : AudioPlayerEvent()
    object ResumeAudio : AudioPlayerEvent()
    data class SeekAudio(val position : Float) : AudioPlayerEvent()
    object CancelAudioDownload : AudioPlayerEvent()
    object StopAudio : AudioPlayerEvent()
}