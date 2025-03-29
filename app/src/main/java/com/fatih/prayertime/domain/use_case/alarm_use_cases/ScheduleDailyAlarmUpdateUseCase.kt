package com.fatih.prayertime.domain.use_case.alarm_use_cases

import android.content.Context
import android.util.Log
import androidx.work.DirectExecutor
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.fatih.prayertime.data.alarm.PrayAlarmWorker
import com.fatih.prayertime.data.alarm.StatisticsAlarmWorker
import com.fatih.prayertime.domain.use_case.formatted_use_cases.FormattedUseCase
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.Executor
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class ScheduleDailyAlarmUpdateUseCase @Inject constructor(
    private val formattedUseCase: FormattedUseCase
) {

    fun executePrayAlarmWorker(context: Context) {

        val workManager = WorkManager.getInstance(context)

        workManager.getWorkInfosForUniqueWorkLiveData("PrayAlarmWorker").observeForever { workInfos ->
            if (workInfos.isEmpty() || workInfos.any { it.state.isFinished }) {
                val workRequest = PeriodicWorkRequestBuilder<PrayAlarmWorker>(1, TimeUnit.HOURS)
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
                val currentTime = System.currentTimeMillis()
                val calendar = Calendar.getInstance().apply {
                    timeInMillis = currentTime
                    add(Calendar.DAY_OF_MONTH, 1)
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }
                
                val randomMinutes = (5..30).random()
                calendar.add(Calendar.MINUTE, randomMinutes)
                
                val nextScheduleTime = calendar.timeInMillis
                val initialDelay = nextScheduleTime - currentTime

                
                Log.d("Schedule", "İstatistik işçisi başlatılıyor - Bir sonraki çalışma: ${formattedUseCase.formatLongToLocalDateTime(nextScheduleTime)}")
                Log.d("Schedule", "İlk çalışma için gecikme: $initialDelay ms (${initialDelay / (1000 * 60)} dakika)")
                
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
                Log.d("Schedule", "Zaten aktif bir istatistik işçisi var: ${workInfos.map { it.state }}")
                Log.d("Schedule", "Bir sonraki çalışma zamanı: ${workInfos.map { it.nextScheduleTimeMillis}}")
            }
        }
    }

}

