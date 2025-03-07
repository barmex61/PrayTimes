package com.fatih.prayertime.domain.use_case.alarm_use_cases

import android.content.Context
import android.util.Log
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.fatih.prayertime.data.alarm.AlarmWorker
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class ScheduleDailyAlarmUpdateUseCase @Inject constructor() {

    fun execute(context: Context) {

        val workManager = WorkManager.getInstance(context)

        workManager.getWorkInfosForUniqueWorkLiveData("AlarmWorker").observeForever { workInfos ->
            if (workInfos.isEmpty() || workInfos.any { it.state.isFinished }) {
                // Eğer hiç yoksa veya tamamlanmışsa yeni iş ekle
                val workRequest = PeriodicWorkRequestBuilder<AlarmWorker>(15, TimeUnit.MINUTES)
                    .addTag("AlarmWorker")
                    .build()

                workManager.enqueueUniquePeriodicWork(
                    "AlarmWorker",
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

}

