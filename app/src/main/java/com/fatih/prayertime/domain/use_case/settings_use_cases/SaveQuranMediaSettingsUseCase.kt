package com.fatih.prayertime.domain.use_case.settings_use_cases

import com.fatih.prayertime.domain.model.QuranMediaSettings
import com.fatih.prayertime.domain.repository.SettingsRepository
import javax.inject.Inject

class SaveQuranMediaSettingsUseCase @Inject constructor(private val settingsRepository: SettingsRepository) {
    suspend operator fun invoke(quranMediaSettings: QuranMediaSettings) = settingsRepository.saveAudioSettings(quranMediaSettings)
} 