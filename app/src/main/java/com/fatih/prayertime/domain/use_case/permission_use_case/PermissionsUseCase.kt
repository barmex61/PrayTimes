package com.fatih.prayertime.domain.use_case.permission_use_case

import android.content.Context
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Build
import androidx.core.content.ContextCompat
import javax.inject.Inject

class PermissionsUseCase @Inject constructor(
    private val context: Context
) {

    fun checkNotificationPermission(): Boolean =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) == PERMISSION_GRANTED
        } else true

    fun checkLocationPermission() : Boolean =
        ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) == PERMISSION_GRANTED ||
        ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PERMISSION_GRANTED

    fun checkAlarmPermission(): Boolean =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            ContextCompat.checkSelfPermission(context, android.Manifest.permission.USE_EXACT_ALARM) == PERMISSION_GRANTED
        } else true

}