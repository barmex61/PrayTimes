package com.fatih.prayertime.domain.use_case.settings_use_cases

import com.fatih.prayertime.domain.model.AudioSettings
import com.fatih.prayertime.domain.repository.SettingsRepository
import javax.inject.Inject

class SaveAudioSettingsUseCase @Inject constructor(private val settingsRepository: SettingsRepository) {
    suspend operator fun invoke(audioSettings: AudioSettings) = settingsRepository.saveAudioSettings(audioSettings)
} 