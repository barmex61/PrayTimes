package com.fatih.prayertime.domain.use_case.sound

import com.fatih.prayertime.domain.model.Sound
import com.fatih.prayertime.domain.repository.SoundRepository
import javax.inject.Inject

class GetSoundsUseCase @Inject constructor(
    private val soundRepository: SoundRepository
) {
    operator fun invoke(selectedSoundUri: String? = null): List<Sound> {
        val defaultSounds = soundRepository.getSystemSounds()
        
        // Seslere isSelected özelliği ekle - seçilen ses URI'sine göre
        return defaultSounds.map { sound ->
            sound.copy(isSelected = sound.uri == selectedSoundUri)
        }
    }
} 