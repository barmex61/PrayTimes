package com.fatih.prayertime.domain.use_case.sound

import com.fatih.prayertime.domain.model.PlaybackState
import com.fatih.prayertime.domain.repository.SoundRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PlaySoundUseCase @Inject constructor(
    private val soundRepository: SoundRepository
) {
    operator fun invoke(uri: String): Flow<PlaybackState> {
        return soundRepository.playSound(uri)
    }
} 