package com.fatih.prayertime.domain.use_case.sound

import com.fatih.prayertime.domain.repository.SoundRepository
import javax.inject.Inject

class StopSoundUseCase @Inject constructor(
    private val soundRepository: SoundRepository
) {
    operator fun invoke() {
        soundRepository.stopSound()
    }
} 