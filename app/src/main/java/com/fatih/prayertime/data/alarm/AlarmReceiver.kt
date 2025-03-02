package com.fatih.prayertime.data.alarm

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.net.toUri
import com.fatih.prayertime.R
import com.fatih.prayertime.presentation.main_activity.view.MainActivity

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val alarmType = intent.getStringExtra("ALARM_TYPE") ?: "Bilinmeyen Alarm"
        val alarmMessage = intent.getStringExtra("ALARM_MESSAGE") ?: "Alarm Çaldı!"
        val enableVibration = intent.getBooleanExtra("ALARM_VIBRATION", true)
        val isSilent = intent.getBooleanExtra("ALARM_IS_SILENT", false)
        val alarmSoundUri =
            if(isSilent) RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            else intent.getStringExtra("ALARM_SOUND_URI")?.toUri() ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        showNotification(context, alarmType, alarmMessage,enableVibration, alarmSoundUri)

    }

    private fun showNotification(context: Context, alarmType: String, alarmMessage: String,enableVibration : Boolean,alarmSoundUri : Uri) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        deleteNotificationChannel(context)
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Alarm Notifications",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Namaz Vakti Alarmları"
            setSound(alarmSoundUri, AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ALARM)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build())
        }
        notificationManager.createNotificationChannel(channel)


        val activityIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(context, 0, activityIntent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
        val vibrationPattern = if (enableVibration) longArrayOf(0, 500, 1000) else longArrayOf(0)

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.alarm_icon)
            .setContentTitle(alarmType)
            .setContentText(alarmMessage)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setSound(alarmSoundUri)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setVibrate(vibrationPattern)
        notificationManager.notify(alarmType.hashCode(),builder.build())
    }

    companion object {
        const val CHANNEL_ID = "alarm_channel"
    }

    private fun deleteNotificationChannel(context: Context) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.deleteNotificationChannel(CHANNEL_ID)

    }
}