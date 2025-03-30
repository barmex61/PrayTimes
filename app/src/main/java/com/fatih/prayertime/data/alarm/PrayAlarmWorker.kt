package com.fatih.prayertime.data.alarm

import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.fatih.prayertime.data.di.WorkerLocation
import com.fatih.prayertime.domain.model.Address
import com.fatih.prayertime.domain.model.PrayerAlarm
import com.fatih.prayertime.domain.model.PrayTimes
import com.fatih.prayertime.domain.use_case.alarm_use_cases.GetAllGlobalAlarmsUseCase
import com.fatih.prayertime.domain.use_case.alarm_use_cases.UpdateGlobalAlarmUseCase
import com.fatih.prayertime.domain.use_case.formatted_use_cases.FormattedUseCase
import com.fatih.prayertime.domain.use_case.location_use_cases.GetLastKnowAddressFromDatabaseUseCase
import com.fatih.prayertime.domain.use_case.location_use_cases.GetLocationAndAddressUseCase
import com.fatih.prayertime.domain.use_case.permission_use_case.PermissionsUseCase
import com.fatih.prayertime.domain.use_case.pray_times_use_cases.DeletePrayTimesBeforeDateUseCase
import com.fatih.prayertime.domain.use_case.pray_times_use_cases.GetMonthlyPrayTimesFromApiUseCase
import com.fatih.prayertime.domain.use_case.pray_times_use_cases.GetDailyPrayTimesWithAddressAndDateUseCase
import com.fatih.prayertime.domain.use_case.pray_times_use_cases.InsertPrayTimeIntoDbUseCase
import com.fatih.prayertime.util.model.state.Status
import com.fatih.prayertime.util.utils.AlarmUtils.getPrayTimeForPrayType
import com.fatih.prayertime.data.alarm.LocationUpdateForegroundService
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import org.threeten.bp.LocalDate


@HiltWorker
class PrayAlarmWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
) : CoroutineWorker(context, workerParams) {


    companion object{
        const val TAG = "AlarmWorker"
    }
    init {
        Log.d(TAG,"İnitialized")
    }

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG,"Started")
            val serviceIntent = Intent(applicationContext, LocationUpdateForegroundService::class.java)
            applicationContext.startForegroundService(serviceIntent)
            return@withContext Result.success()
        } catch (e: Exception) {
            Log.e(TAG,"Error starting foreground service: ${e.message}", e)
            return@withContext Result.failure()
        }
    }

}