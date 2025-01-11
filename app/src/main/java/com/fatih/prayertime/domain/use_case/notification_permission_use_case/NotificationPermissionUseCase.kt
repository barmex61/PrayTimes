package com.fatih.prayertime.domain.use_case.notification_permission_use_case

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.core.content.ContextCompat
import javax.inject.Inject

class NotificationPermissionUseCase @Inject constructor(
    private val context: Context
) {

    fun checkPermission(): Boolean =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) == PERMISSION_GRANTED
        } else true

}