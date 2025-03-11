package com.fatih.prayertime.data.alarm

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.fatih.prayertime.domain.model.Address
import com.fatih.prayertime.domain.use_case.alarm_use_cases.UpdateStatisticsAlarmUseCase
import com.fatih.prayertime.domain.use_case.formatted_use_cases.FormattedUseCase
import com.fatih.prayertime.domain.use_case.location_use_cases.GetLastKnowAddressFromDatabaseUseCase
import com.fatih.prayertime.domain.use_case.pray_times_use_cases.GetDailyPrayTimesWithAddressAndDateUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import org.threeten.bp.LocalDate

@HiltWorker
class StatisticsAlarmWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val updateStatisticsAlarmUseCase: UpdateStatisticsAlarmUseCase,
    private val getDailyPrayTimesWithAddressAndDateUseCase: GetDailyPrayTimesWithAddressAndDateUseCase,
    private val getLastKnowAddressFromDatabaseUseCase: GetLastKnowAddressFromDatabaseUseCase,
    private val formattedUseCase: FormattedUseCase
): CoroutineWorker(context, workerParams){


    override suspend fun doWork(): Result {
        val lastKnownAddress = getLastKnowAddressFromDatabaseUseCase()?:return Result.failure()
        updateStatisticsAlarm(lastKnownAddress)
        println("Statistics Alarm Worker")
        return Result.success()
    }


    private suspend fun updateStatisticsAlarm(lastKnownAddress: Address){
        val localDateNow = LocalDate.now()
        val localDateString = formattedUseCase.formatOfPatternDDMMYYYY(localDateNow)
        val prayTimes = getDailyPrayTimesWithAddressAndDateUseCase(lastKnownAddress,localDateString)?:return
        updateStatisticsAlarmUseCase.updateStatisticsAlarms(prayTimes)
    }
}