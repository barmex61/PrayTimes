package com.fatih.prayertime.domain.use_case.permission_use_case

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import javax.inject.Inject

class PermissionUseCase @Inject constructor(
    private val context: Context
) {

    fun checkPermission(permission: String): Boolean =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContextCompat.checkSelfPermission(context, permission) == PERMISSION_GRANTED
        } else true

    fun showRationale(activity: Activity, permission: String): Boolean =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)
        } else false

    fun requestPermission(activity: Activity, permission: String, requestCode: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            activity.requestPermissions(arrayOf(permission), requestCode)
        }
    }
}