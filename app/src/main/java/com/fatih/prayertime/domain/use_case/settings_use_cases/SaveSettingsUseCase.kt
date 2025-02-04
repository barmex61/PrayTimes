package com.fatih.prayertime.domain.use_case.settings_use_cases

import com.fatih.prayertime.domain.model.Settings
import com.fatih.prayertime.domain.repository.SettingsRepository
import javax.inject.Inject

class SaveSettingsUseCase @Inject constructor(private val settingsRepository: SettingsRepository) {
    suspend operator fun invoke(settings : Settings) = settingsRepository.saveSettings(settings)
}