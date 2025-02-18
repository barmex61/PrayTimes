package com.fatih.prayertime.domain.use_case.alarm_use_cases

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.fatih.prayertime.data.alarm.AlarmWorker
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class ScheduleDailyAlarmUpdateUseCase @Inject constructor() {

    fun execute(context: Context) {

        val workRequest = PeriodicWorkRequestBuilder<AlarmWorker>(15,TimeUnit.MINUTES)
            .addTag("AlarmWorker")
            .build()


        WorkManager.getInstance(context)
            .enqueueUniquePeriodicWork(
                "AlarmWorker",
                ExistingPeriodicWorkPolicy.KEEP,
                workRequest
            )/*
            .state.observeForever {
                Log.d("state: $it")
            }

        Log.d("executed") */
    }
    /*
    private fun calculateInitialDelay(): Long {
        val now = LocalDateTime.now()
        val midnight = now.toLocalDate().atStartOfDay().plusDays(1)
        return Duration.between(now, midnight).toMillis() + Duration.ofMinutes(30).toMillis()
    }  */

}

