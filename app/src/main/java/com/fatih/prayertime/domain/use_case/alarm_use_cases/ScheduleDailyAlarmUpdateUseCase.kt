package com.fatih.prayertime.domain.use_case.alarm_use_cases

import android.content.Context
import android.util.Log
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.fatih.prayertime.data.alarm.PrayAlarmWorker
import com.fatih.prayertime.data.alarm.StatisticsAlarmWorker
import java.util.Calendar
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class ScheduleDailyAlarmUpdateUseCase @Inject constructor() {

    fun executePrayAlarmWorker(context: Context) {

        val workManager = WorkManager.getInstance(context)

        workManager.getWorkInfosForUniqueWorkLiveData("PrayAlarmWorker").observeForever { workInfos ->
            if (workInfos.isEmpty() || workInfos.any { it.state.isFinished }) {
                // Eğer hiç yoksa veya tamamlanmışsa yeni iş ekle
                val workRequest = PeriodicWorkRequestBuilder<PrayAlarmWorker>(15, TimeUnit.MINUTES)
                    .addTag("PrayAlarmWorker")
                    .build()

                workManager.enqueueUniquePeriodicWork(
                    "PrayAlarmWorker",
                    ExistingPeriodicWorkPolicy.KEEP,
                    workRequest
                )
                Log.d("Schedule", "Created new work ")
            } else {
                Log.d("Schedule", "Already there is a job: ${workInfos.map { it.state }}")
                Log.d("Schedule", "Next schedule time: ${workInfos.map { it.nextScheduleTimeMillis}}")
            }
        }
    }

    fun executeStatisticsAlarmWorker(context: Context) {
        val workManager = WorkManager.getInstance(context)
        workManager.getWorkInfosForUniqueWorkLiveData("StatisticsAlarmWorker").observeForever { workInfos ->
            if (workInfos.isEmpty() || workInfos.any { it.state.isFinished }) {
                println("yessir")
                val currentTime = System.currentTimeMillis()
                val calendar = Calendar.getInstance().apply {
                    timeInMillis = currentTime
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                    if (timeInMillis <= currentTime) {
                        add(Calendar.DAY_OF_MONTH, 1)
                    }
                }
                val initialDelay = calendar.timeInMillis - currentTime + 1_800_000

                val workRequest = PeriodicWorkRequestBuilder<StatisticsAlarmWorker>(24, TimeUnit.HOURS)
                    .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
                    .addTag("StatisticsAlarmWorker")
                    .build()

                workManager.enqueueUniquePeriodicWork(
                    "StatisticsAlarmWorker",
                    ExistingPeriodicWorkPolicy.REPLACE,
                    workRequest
                )
                 Log.d("Schedule", "Statistics worker created new work with initial delay: $initialDelay ms")
            } else {
                Log.d("Schedule", "Already there is a job: ${workInfos.map { it.state }}")
            }
        }
    }

}

