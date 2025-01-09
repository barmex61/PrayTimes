package com.fatih.prayertime.presentation.main_activity.viewmodel

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.ComponentActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fatih.prayertime.domain.use_case.get_network_state_use_case.GetNetworkStateUseCase
import com.fatih.prayertime.util.NetworkState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor(
    private val getNetworkStateUseCase: GetNetworkStateUseCase
) : ViewModel() {

    //Network-State

    private val _networkState = MutableStateFlow<NetworkState>(NetworkState.Disconnected)
    val networkState: StateFlow<NetworkState> = _networkState

    init {
        viewModelScope.launch(Dispatchers.IO){
            getNetworkStateUseCase().collect{
                _networkState.emit(it)
            }
        }
    }

    //Permissions

    private val _permissionGranted = MutableStateFlow<Boolean>(false)
    val permissionGranted: StateFlow<Boolean> = _permissionGranted
    private val _showGoToSettings = MutableStateFlow<Boolean>(false)
    val showGoToSettings: StateFlow<Boolean> = _showGoToSettings
    private val _showPermissionRequest = MutableStateFlow<Boolean>(true)
    val showPermissionRequest: StateFlow<Boolean> = _showPermissionRequest

    val locationPermissions = arrayOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    fun checkPermissions(context: Context) {
        val isAllPermissionsGranted = locationPermissions.all {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }
        _permissionGranted.value = isAllPermissionsGranted
        _showPermissionRequest.value = !isAllPermissionsGranted
        _showGoToSettings.value = false
    }

    fun onPermissionsResult(permissions: Map<String, Boolean>,activity : ComponentActivity) {
        if (permissions.all { it.value }) {
            _permissionGranted.value = true
            _showGoToSettings.value = false
            _showPermissionRequest.value = false
        } else {
            val shouldShowRationale = locationPermissions.any {
                androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale(
                    activity,
                    it
                )
            }
            _showPermissionRequest.value = true
            _showGoToSettings.value = !shouldShowRationale
        }
    }
}