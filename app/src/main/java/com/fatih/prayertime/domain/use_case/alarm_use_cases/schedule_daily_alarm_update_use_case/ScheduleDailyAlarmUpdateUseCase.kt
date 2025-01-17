package com.fatih.prayertime.domain.use_case.alarm_use_cases.schedule_daily_alarm_update_use_case

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.fatih.prayertime.data.alarm.AlarmWorker
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class ScheduleDailyAlarmUpdateUseCase @Inject constructor() {

    fun execute(context: Context) {
        val workRequest = PeriodicWorkRequestBuilder<AlarmWorker>(2, TimeUnit.HOURS)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "DailyAlarmUpdate",
            ExistingPeriodicWorkPolicy.UPDATE,
            workRequest
        )

    }
    /*
    private fun calculateInitialDelay(): Long {
        val now = LocalDateTime.now()
        val midnight = now.toLocalDate().atStartOfDay().plusDays(1)
        return Duration.between(now, midnight).toMillis() + Duration.ofMinutes(30).toMillis()
    }  */

}

