package com.fatih.prayertime.presentation.main_activity.viewmodel

import android.Manifest
import androidx.activity.ComponentActivity
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fatih.prayertime.domain.model.Settings
import com.fatih.prayertime.domain.model.ThemeOption
import com.fatih.prayertime.domain.use_case.location_use_cases.RemoveLocationCallbackUseCase
import com.fatih.prayertime.domain.use_case.network_state_use_cases.GetNetworkStateUseCase
import com.fatih.prayertime.domain.use_case.permission_use_case.IsPowerSavingEnabledUseCase
import com.fatih.prayertime.domain.use_case.permission_use_case.PermissionsUseCase
import com.fatih.prayertime.domain.use_case.settings_use_cases.GetSettingsUseCase
import com.fatih.prayertime.domain.use_case.settings_use_cases.SaveSettingsUseCase
import com.fatih.prayertime.util.NetworkState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor(
    private val getNetworkStateUseCase: GetNetworkStateUseCase,
    private val permissionsUseCase: PermissionsUseCase,
    private val removeLocationCallbackUseCase: RemoveLocationCallbackUseCase,
    private val isPowerSavingEnabledUseCase: IsPowerSavingEnabledUseCase,
    private val getSettingsUseCase: GetSettingsUseCase,
    private val saveSettingsUseCase: SaveSettingsUseCase

) : ViewModel() {

    //Network-State

    private val _networkState = MutableStateFlow<NetworkState>(NetworkState.Disconnected)
    val networkState: StateFlow<NetworkState> = _networkState

    //Permissions

    val locationPermissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    private val _isLocationPermissionGranted = MutableStateFlow(false)
    val isLocationPermissionGranted: StateFlow<Boolean> = _isLocationPermissionGranted

    private val _showLocationPermissionRationale = MutableStateFlow(false)
    val showLocationPermissionRationale: StateFlow<Boolean> = _showLocationPermissionRationale


    fun checkLocationPermission(){
        _isLocationPermissionGranted.value = permissionsUseCase.checkLocationPermission()
    }

    fun onLocationPermissionResult(permissionResult : Map<String,Boolean>, activity: ComponentActivity){
        if(permissionResult.values.all { it }){
            _isLocationPermissionGranted.value = true
            _showLocationPermissionRationale.value = false
        }else{
            _isLocationPermissionGranted.value = false
            _showLocationPermissionRationale.value = permissionsUseCase.showLocationPermissionRationale(activity)
        }
    }

    // Notification Permission

    val isNotificationPermissionGranted = mutableStateOf(false)

    fun checkNotificationPermission() {
        isNotificationPermissionGranted.value = permissionsUseCase.checkNotificationPermission()
    }

    // Power-Saving

    private val _powerSavingState = MutableStateFlow<Boolean?>(null)
    val powerSavingState: StateFlow<Boolean?> = _powerSavingState

    fun checkPowerSavingMode() {
        viewModelScope.launch {
            _powerSavingState.value = isPowerSavingEnabledUseCase()
        }
    }

    // Settings

    private val _settingsState = MutableStateFlow(Settings())
    val settingsState = _settingsState.asStateFlow()

    fun updateTheme(theme: ThemeOption) = viewModelScope.launch {
        val updatedSettings = _settingsState.value.copy(selectedTheme = theme)
        saveSettingsUseCase(updatedSettings)
    }

    fun toggleVibration() = viewModelScope.launch {
        val updatedSettings = _settingsState.value.copy(vibrationEnabled = !_settingsState.value.vibrationEnabled)
        saveSettingsUseCase(updatedSettings)
    }

    fun toggleCuma() = viewModelScope.launch {
        val updatedSettings = _settingsState.value.copy(silenceWhenCuma = !_settingsState.value.silenceWhenCuma)
        saveSettingsUseCase(updatedSettings)
    }

    fun togglePrayerNotification(prayerName: String) = viewModelScope.launch {

    }

    init {
        viewModelScope.launch(Dispatchers.IO){
            launch {
                getNetworkStateUseCase().filter{
                    it != _networkState.value
                }.collectLatest {
                    _networkState.value = it
                }
            }
            launch {
                getSettingsUseCase.invoke().distinctUntilChanged()
                    .collectLatest{ settings ->
                        println("settings $settings")
                        _settingsState.value = settings
                }
            }
        }
        checkNotificationPermission()
        checkPowerSavingMode()
    }

    override fun onCleared() {
        removeLocationCallbackUseCase()
        super.onCleared()
    }
}