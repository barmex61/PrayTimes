package com.fatih.prayertime.data.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import com.fatih.prayertime.data.settings.SettingsDataStore
import com.fatih.prayertime.domain.model.PrayerAlarm
import com.fatih.prayertime.domain.model.PrayTimes
import com.fatih.prayertime.domain.use_case.formatted_use_cases.FormattedUseCase
import com.fatih.prayertime.util.extensions.toPrayTimePair
import com.fatih.prayertime.util.model.enums.AlarmType
import com.fatih.prayertime.util.model.enums.PrayTimesString
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

class AlarmScheduler @Inject constructor(
    private val context: Context,
    private val settingsDataStore: SettingsDataStore,
    private val formattedUseCase: FormattedUseCase
) {

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    private fun schedulePrayAlarm(prayerAlarm: PrayerAlarm) {
        val settings = runBlocking { settingsDataStore.settings.first() }
        val muteAtFridayPrayer = settings.silenceWhenCuma && formattedUseCase.isFriday(prayerAlarm.alarmTimeString) && prayerAlarm.alarmType == PrayTimesString.Noon.name
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("ALARM_TYPE",AlarmType.PRAY.name)
            putExtra("PRAY_TYPE", prayerAlarm.alarmType)
            putExtra("VIBRATION",settings.vibrationEnabled)
            putExtra("SOUND_URI",prayerAlarm.soundUri)
            putExtra("IS_SILENT",muteAtFridayPrayer)
        }
        val requestCode = ("prayer " + prayerAlarm.alarmType).hashCode()
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            prayerAlarm.alarmTime,
            pendingIntent
        )
        Log.d("AlarmScheduler", "Alarm set for ${prayerAlarm.alarmTimeString} muteAtFridayPrayer: $muteAtFridayPrayer")
    }

    private fun scheduleStatisticsAlarm(alarmTime : Long,alarmDate : String,alarmType : String){
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("ALARM_TYPE",AlarmType.STATISTICS.name)
            putExtra("PRAY_TYPE",alarmType)
            putExtra("ALARM_DATE",alarmDate)
        }
        val requestCode = ("statistics $alarmType").hashCode()
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            alarmTime,
            pendingIntent
        )
        Log.d("AlarmScheduler", "Statistics alarm set for $alarmTime ${formattedUseCase.formatLongToLocalDateTime(alarmTime)}")

    }

    private fun cancel(alarm: PrayerAlarm) {
        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alarm.alarmType.hashCode(),
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_CANCEL_CURRENT
        )

        alarmManager.cancel(pendingIntent)
    }

    fun updatePrayAlarm(alarm: PrayerAlarm){
        if(alarm.isEnabled){
            if(alarm.alarmTime > System.currentTimeMillis()){
                schedulePrayAlarm(alarm)
            }
        }else{
            cancel(alarm)
        }
    }

    fun updateStatisticsAlarmForPrayTime(prayTimes: PrayTimes){
        val prayTimePair = prayTimes.toPrayTimePair(30)
        prayTimePair.forEach { (prayType,prayTime) ->
            scheduleStatisticsAlarm(prayTime,prayTimes.date,prayType)
        }
    }

    fun updateStatisticsAlarmForPrayType(prayTime: Long,alarmDate: String,alarmType: String){
        scheduleStatisticsAlarm(alarmTime = prayTime, alarmDate = alarmDate, alarmType = alarmType)
    }

}


