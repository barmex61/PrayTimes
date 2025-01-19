package com.fatih.prayertime.domain.use_case.alarm_use_cases.schedule_daily_alarm_update_use_case

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.fatih.prayertime.data.alarm.AlarmWorker
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class ScheduleDailyAlarmUpdateUseCase @Inject constructor() {

    fun execute(context: Context) {

        val workRequest = PeriodicWorkRequestBuilder<AlarmWorker>(1,TimeUnit.HOURS)
            .addTag("DailyAlarmUpdate")
            .setConstraints(
                Constraints.Builder()
                    .setRequiresBatteryNotLow(false)
                    .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                    .build()
            )
            .build()

        WorkManager.getInstance(context)
            .enqueueUniquePeriodicWork(
                "DailyAlarmUpdate",
                ExistingPeriodicWorkPolicy.UPDATE,
                workRequest
            )
            .state.observeForever {
                println("state: $it")
            }
        println("executed")
    }
    /*
    private fun calculateInitialDelay(): Long {
        val now = LocalDateTime.now()
        val midnight = now.toLocalDate().atStartOfDay().plusDays(1)
        return Duration.between(now, midnight).toMillis() + Duration.ofMinutes(30).toMillis()
    }  */

}

