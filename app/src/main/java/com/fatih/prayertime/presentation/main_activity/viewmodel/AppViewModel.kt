package com.fatih.prayertime.presentation.main_activity.viewmodel

import android.Manifest
import androidx.activity.ComponentActivity
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fatih.prayertime.domain.use_case.get_network_state_use_case.GetNetworkStateUseCase
import com.fatih.prayertime.domain.use_case.permission_use_case.PermissionsUseCase
import com.fatih.prayertime.util.NetworkState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor(
    private val getNetworkStateUseCase: GetNetworkStateUseCase,
    private val permissionsUseCase: PermissionsUseCase,
) : ViewModel() {

    //Network-State

    private val _networkState = MutableStateFlow<NetworkState>(NetworkState.Disconnected)
    val networkState: StateFlow<NetworkState> = _networkState

    init {
        viewModelScope.launch(Dispatchers.IO){
            getNetworkStateUseCase().filter{
                it != _networkState.value
            }.collectLatest {
                _networkState.value = it
            }
        }
    }

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

    init {
        checkNotificationPermission()
    }

}