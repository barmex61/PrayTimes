package com.fatih.prayertime.data.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import com.fatih.prayertime.R
import com.fatih.prayertime.data.settings.SettingsDataStore
import com.fatih.prayertime.domain.model.GlobalAlarm
import com.fatih.prayertime.domain.model.PrayTimes
import com.fatih.prayertime.domain.use_case.formatted_use_cases.FormattedUseCase
import com.fatih.prayertime.util.model.enums.AlarmType
import com.fatih.prayertime.util.model.enums.PrayTimesString
import com.fatih.prayertime.util.utils.AlarmUtils.getAlarmTimeForPrayTimes
import com.fatih.prayertime.util.utils.AlarmUtils.getAlarmTimeLongForPrayTimes
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

class AlarmScheduler @Inject constructor(
    private val context: Context,
    private val settingsDataStore: SettingsDataStore,
    private val formattedUseCase: FormattedUseCase
) {

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    private fun schedulePrayAlarm(alarm: GlobalAlarm) {
        val settings = runBlocking { settingsDataStore.settings.first() }
        val muteAtFridayPrayer = settings.silenceWhenCuma && formattedUseCase.isFriday(alarm.alarmTimeString) && alarm.alarmType == PrayTimesString.Noon.name
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("ALARM_TYPE",AlarmType.PRAY.name)
            putExtra("ALARM_PRAY_TYPE", alarm.alarmType)
            putExtra("ALARM_MESSAGE", alarm.alarmType)
            putExtra("ALARM_VIBRATION",settings.vibrationEnabled)
            putExtra("ALARM_SOUND_URI",alarm.soundUri)
            putExtra("ALARM_IS_SILENT",muteAtFridayPrayer)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alarm.alarmType.hashCode(),
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            alarm.alarmTime,
            pendingIntent
        )
        Log.d("AlarmScheduler", "Alarm set for ${alarm.alarmTimeString} muteAtFridayPrayer: $muteAtFridayPrayer")
    }

    private fun scheduleStatisticsAlarm(prayTimes: PrayTimes){
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("ALARM_TYPE",AlarmType.STATISTICS.name)
        }

        val alarmTimeList : List<Long> = getAlarmTimeLongForPrayTimes(prayTimes,formattedUseCase)
        alarmTimeList.forEach { alarmTime ->
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                alarmTime.toInt(),
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

    }

    private fun cancel(alarm: GlobalAlarm) {
        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alarm.alarmType.hashCode(),
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_CANCEL_CURRENT
        )

        alarmManager.cancel(pendingIntent)
    }

    fun updatePrayAlarm(alarm: GlobalAlarm){
        if(alarm.isEnabled){
            //if(alarm.alarmTime > System.currentTimeMillis()) schedule(alarm)
            schedulePrayAlarm(alarm)
        }else{
            cancel(alarm)
        }
    }

    fun updateStatisticsAlarm(prayTimes: PrayTimes){
        scheduleStatisticsAlarm(prayTimes)
    }
}