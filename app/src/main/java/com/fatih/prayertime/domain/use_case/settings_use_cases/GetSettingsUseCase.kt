package com.fatih.prayertime.domain.use_case.settings_use_cases

import com.fatih.prayertime.domain.repository.SettingsRepository
import javax.inject.Inject

class GetSettingsUseCase @Inject constructor(private val settingsRepository : SettingsRepository) {
    operator fun invoke() = settingsRepository.getSettings()
}