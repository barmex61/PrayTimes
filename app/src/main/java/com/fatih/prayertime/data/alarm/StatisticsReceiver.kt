package com.fatih.prayertime.data.alarm

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.fatih.prayertime.R
import com.fatih.prayertime.data.local.entity.PrayerStatisticsEntity
import com.fatih.prayertime.domain.use_case.alarm_use_cases.UpdateStatisticsAlarmUseCase
import com.fatih.prayertime.domain.use_case.formatted_use_cases.FormattedUseCase
import com.fatih.prayertime.domain.use_case.location_use_cases.GetLastKnowAddressFromDatabaseUseCase
import com.fatih.prayertime.domain.use_case.pray_times_use_cases.GetDailyPrayTimesWithAddressAndDateUseCase
import com.fatih.prayertime.domain.use_case.statistics_use_cases.InsertPlayerStatisticsUseCase
import com.fatih.prayertime.domain.use_case.statistics_use_cases.IsStatisticsExistsUseCase
import com.fatih.prayertime.util.extensions.addMinutesToLong
import com.fatih.prayertime.util.extensions.nextPrayTimeLong
import com.fatih.prayertime.util.utils.StatisticsUtils.generateStatisticsId
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@AndroidEntryPoint
class StatisticsReceiver : BroadcastReceiver() {

    @Inject
    lateinit var insertPlayerStatisticsUseCase: InsertPlayerStatisticsUseCase
    @Inject
    lateinit var isStatisticsExistsUseCase: IsStatisticsExistsUseCase
    @Inject
    lateinit var updateStatisticsUseCase: InsertPlayerStatisticsUseCase
    @Inject
    lateinit var updateStatisticsAlarmUseCase: UpdateStatisticsAlarmUseCase
    @Inject
    lateinit var formattedUseCase: FormattedUseCase
    @Inject
    lateinit var getDailyPrayTimesWithAddressAndDateUseCase: GetDailyPrayTimesWithAddressAndDateUseCase
    @Inject
    lateinit var getLastKnowAddressFromDatabaseUseCase: GetLastKnowAddressFromDatabaseUseCase

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        val prayType = intent.getStringExtra("PRAY_TYPE") ?: ""
        val alarmDate = intent.getStringExtra("ALARM_DATE") ?: ""
        val notificationId = intent.getIntExtra("NOTIFICATION_ID",-1)
        println("statisticsReceiver $notificationId")
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val alarmTime = System.currentTimeMillis()
        val prayTimes = runBlocking(Dispatchers.IO) {
           val lastKnownAddress = getLastKnowAddressFromDatabaseUseCase()?:return@runBlocking null
           getDailyPrayTimesWithAddressAndDateUseCase(lastKnownAddress,alarmDate)
        }?:return
        val statisticsId = generateStatisticsId(prayType,alarmDate)
        val statisticsEntity = when (action) {
            context.getString(R.string.yes) -> {
                PrayerStatisticsEntity(id = statisticsId,prayerType = prayType, date = alarmDate, isCompleted = true)
            }
            context.getString(R.string.no) -> {
                val nextAlarmTime = alarmTime.addMinutesToLong(30L)
                val nextPrayTime = prayTimes.nextPrayTimeLong(prayType)
                if (nextAlarmTime < nextPrayTime){
                    updateStatisticsAlarmUseCase.updateStatisticsAlarm(nextAlarmTime,alarmDate,prayType)
                }
                PrayerStatisticsEntity(id = statisticsId,prayerType = prayType, date = alarmDate, isCompleted = false)
            }
            else -> return
        }
        runBlocking(Dispatchers.IO) {
            if (isStatisticsExistsUseCase(prayType, alarmDate)) {
                updateStatisticsUseCase(statisticsEntity)
            }else{
                insertPlayerStatisticsUseCase(statisticsEntity)
            }
        }
        notificationManager.cancel(notificationId)
    }
}