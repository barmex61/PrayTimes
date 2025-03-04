package com.fatih.prayertime.data.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.AlarmManagerCompat
import com.fatih.prayertime.data.settings.SettingsDataStore
import com.fatih.prayertime.domain.model.GlobalAlarm
import com.fatih.prayertime.domain.use_case.formatted_use_cases.FormattedUseCase
import com.fatih.prayertime.util.PrayTimesString
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

class AlarmScheduler @Inject constructor(
    private val context: Context,
    private val settingsDataStore: SettingsDataStore,
    private val formattedUseCase: FormattedUseCase
) {

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    private fun schedule(alarm: GlobalAlarm) {
        val settings = runBlocking { settingsDataStore.settings.first() }
        val muteAtFridayPrayer = settings.silenceWhenCuma && formattedUseCase.isFriday(alarm.alarmTimeString) && alarm.alarmType == PrayTimesString.Noon.name
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("ALARM_TYPE", alarm.alarmType)
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

    fun update(alarm: GlobalAlarm){
        if(alarm.isEnabled){
            //if(alarm.alarmTime > System.currentTimeMillis()) schedule(alarm)
            schedule(alarm)
        }else{
            cancel(alarm)
        }
    }
}