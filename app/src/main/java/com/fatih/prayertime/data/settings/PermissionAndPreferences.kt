package com.fatih.prayertime.data.settings

import android.Manifest
import android.os.Build
import androidx.compose.runtime.mutableStateOf
import com.fatih.prayertime.domain.use_case.network_state_use_cases.GetNetworkStateUseCase
import com.fatih.prayertime.domain.use_case.permission_use_case.IsPowerSavingEnabledUseCase
import com.fatih.prayertime.domain.use_case.permission_use_case.PermissionsUseCase
import com.fatih.prayertime.util.model.state.NetworkState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class PermissionAndPreferences @Inject constructor(
    private val permissionUseCase : PermissionsUseCase,
    private val getNetworkStateUseCase: GetNetworkStateUseCase,
    private val isPowerSavingEnabledUseCase: IsPowerSavingEnabledUseCase
) {


    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)


    // ----NOTIFICATION----
    val notificationPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        Manifest.permission.POST_NOTIFICATIONS
    } else {
        null
    }

    private val _isNotificationPermissionGranted : MutableStateFlow<Boolean> = MutableStateFlow(permissionUseCase.checkNotificationPermission())
    val isNotificationPermissionGranted : StateFlow<Boolean> = _isNotificationPermissionGranted

    fun checkNotificationPermission() {
        _isNotificationPermissionGranted.value = permissionUseCase.checkNotificationPermission()

    }

    val alarmPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
        Manifest.permission.USE_EXACT_ALARM
    } else {
        null
    }

    private val _isAlarmPermissionGranted : MutableStateFlow<Boolean> = MutableStateFlow(permissionUseCase.checkAlarmPermission())
    val isAlarmPermissionGranted : StateFlow<Boolean> = _isAlarmPermissionGranted

    fun checkAlarmPermission() {
        _isAlarmPermissionGranted.value = permissionUseCase.checkAlarmPermission()
    }


    // ----NETWORK STATE----


    private val _networkState = MutableStateFlow<NetworkState>(NetworkState.Disconnected)
    val networkState: StateFlow<NetworkState> = _networkState

    //--POWER-SAVİNG-MODE--

    private val _powerSavingState = MutableStateFlow<Boolean?>(null)
    val powerSavingState: StateFlow<Boolean?> = _powerSavingState

    fun checkPowerSavingMode(){
        _powerSavingState.value = isPowerSavingEnabledUseCase()
    }

    //--LOCATION-PERMISSIONS--


    val locationPermissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    private val _isLocationPermissionGranted = MutableStateFlow(false)
    val isLocationPermissionGranted: StateFlow<Boolean> = _isLocationPermissionGranted


    fun checkLocationPermission(){
        _isLocationPermissionGranted.value = permissionUseCase.checkLocationPermission()
    }

    fun onPermissionResult(permissionResult : Map<String,Boolean>){
        val result = permissionResult.values.all { it }
        _isLocationPermissionGranted.value = result
        _isNotificationPermissionGranted.value = result
        _isAlarmPermissionGranted.value = result
    }


    init {
        checkNotificationPermission()
        checkPowerSavingMode()
        checkAlarmPermission()
        scope.launch {
            launch {
                getNetworkStateUseCase().collectLatest {
                    _networkState.value = it
                }
            }
        }
    }

}