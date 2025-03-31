package com.fatih.prayertime.presentation.main_activity

import android.Manifest
import android.os.Build
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

    fun getPermissionList() : List<String>{
        val postNotifications = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) Manifest.permission.POST_NOTIFICATIONS else null
        val scheduleExactAlarm = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) Manifest.permission.SCHEDULE_EXACT_ALARM else null
        val permissions = mutableListOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
        if (postNotifications != null) permissions.add(postNotifications)
        if (scheduleExactAlarm != null) permissions.add(scheduleExactAlarm)
        return permissions
    }
}