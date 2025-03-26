package com.fatih.prayertime.presentation.main_activity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fatih.prayertime.data.settings.PermissionAndPreferences
import com.fatih.prayertime.domain.model.Settings
import com.fatih.prayertime.domain.use_case.settings_use_cases.GetSettingsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    getSettingsUseCase: GetSettingsUseCase,
    val permissionAndPreferences: PermissionAndPreferences
): ViewModel(){

    val settingsState = getSettingsUseCase().stateIn(viewModelScope, SharingStarted.WhileSubscribed(), Settings())
}