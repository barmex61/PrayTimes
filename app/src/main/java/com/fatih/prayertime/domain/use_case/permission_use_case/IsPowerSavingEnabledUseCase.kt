package com.fatih.prayertime.domain.use_case.permission_use_case

import android.content.Context
import android.os.Build
import android.os.PowerManager
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class IsPowerSavingEnabledUseCase @Inject constructor(@ApplicationContext private val context: Context) {

    operator fun invoke(): Boolean{
        return if (Build.MANUFACTURER.equals("Xiaomi", ignoreCase = true)) {
            isXiaomiWithOptimizations()
        } else {
            false
        }
    }

    private fun isXiaomiWithOptimizations() : Boolean{
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        return !powerManager.isIgnoringBatteryOptimizations(context.packageName)
    }
}