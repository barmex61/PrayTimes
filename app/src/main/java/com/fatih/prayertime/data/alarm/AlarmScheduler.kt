package com.fatih.prayertime.data.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.AlarmManagerCompat
import com.fatih.prayertime.domain.model.GlobalAlarm
import javax.inject.Inject

class AlarmScheduler @Inject constructor(private val context: Context) {

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    private fun schedule(alarm: GlobalAlarm) {
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("ALARM_TYPE", alarm.alarmType)
            putExtra("ALARM_MESSAGE", alarm.alarmType)
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
        Log.d("AlarmScheduler", "Alarm set for ${alarm.alarmTimeString}")
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
        if(alarm.isEnabled ){
            if(alarm.alarmTime > System.currentTimeMillis()) schedule(alarm)
        }else{
            cancel(alarm)
        }
    }
}