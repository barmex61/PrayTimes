package com.fatih.namazvakitleri.presentation.main_activity.viewmodel

import android.Manifest
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.compose.runtime.MutableState
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fatih.namazvakitleri.domain.model.Address
import com.fatih.namazvakitleri.domain.use_case.get_location_and_adress_use_case.GetLocationAndAddressUseCase
import com.fatih.namazvakitleri.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val getLocationAndAddressUseCase: GetLocationAndAddressUseCase,
) : ViewModel() {

    //Location

    private val _locationAndAddress = MutableStateFlow<Resource<Address>>(Resource.loading())
    val locationAndAddress: StateFlow<Resource<Address>> = _locationAndAddress

    fun getLocationAndAddress() = viewModelScope.launch {
        getLocationAndAddressUseCase().collect {
            _locationAndAddress.value = it
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