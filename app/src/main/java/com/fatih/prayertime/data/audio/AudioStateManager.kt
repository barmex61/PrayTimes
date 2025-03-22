package com.fatih.prayertime.data.audio

import com.fatih.prayertime.util.model.state.AudioPlayerState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AudioStateManager @Inject constructor() {
    private val _audioPlayerState = MutableStateFlow(AudioPlayerState())
    val audioPlayerState: StateFlow<AudioPlayerState> = _audioPlayerState.asStateFlow()
    
    val audioState: AudioPlayerState
        get() = _audioPlayerState.value

    fun updateState(update: AudioPlayerState.() -> AudioPlayerState) {
        _audioPlayerState.value = update(_audioPlayerState.value)
    }
} 