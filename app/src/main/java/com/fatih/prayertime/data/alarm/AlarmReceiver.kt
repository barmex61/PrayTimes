package com.fatih.prayertime.data.alarm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.net.Uri
import android.util.Log
import androidx.compose.ui.res.stringResource
import androidx.core.app.NotificationCompat
import androidx.core.net.toUri
import com.fatih.prayertime.R
import com.fatih.prayertime.presentation.main_activity.MainActivity
import com.fatih.prayertime.util.model.enums.AlarmType

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val alarmType = intent.getStringExtra("ALARM_TYPE")
        Log.d("AlarmReceiver","ss")

        when(alarmType){
            AlarmType.PRAY.name ->{
                val alarmPrayType = intent.getStringExtra("ALARM_PRAY_TYPE") ?: "Bilinmeyen Alarm"
                val alarmMessage = intent.getStringExtra("ALARM_MESSAGE") ?: "Alarm Çaldı!"
                val enableVibration = intent.getBooleanExtra("ALARM_VIBRATION", true)
                val isSilent = intent.getBooleanExtra("ALARM_IS_SILENT", false)
                val alarmSoundUri =
                    if(isSilent) RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                    else intent.getStringExtra("ALARM_SOUND_URI")?.toUri() ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
                showNotificationForPray(context, alarmPrayType, alarmMessage,enableVibration, alarmSoundUri)
            }
            AlarmType.STATISTICS.name ->{
                Log.d("AlarmReceiver","ss")
                showNotificationForStatistics(context)
            }
        }
    }

    private fun showNotificationForPray(context: Context, alarmPrayType: String, alarmMessage: String,enableVibration : Boolean,alarmSoundUri : Uri) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        deleteNotificationChannel(context)
        val channel = NotificationChannel(
            PRAY_CHANNEL_ID,
            "Pray Notifications",
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

        val builder = NotificationCompat.Builder(context, PRAY_CHANNEL_ID)
            .setSmallIcon(R.drawable.alarm_icon)
            .setContentTitle(alarmPrayType)
            .setContentText(alarmMessage)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setSound(alarmSoundUri)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setVibrate(vibrationPattern)
        notificationManager.notify(alarmPrayType.hashCode(),builder.build())
    }

    private fun showNotificationForStatistics(context: Context){
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channel = NotificationChannel(
            STATISTICS_CHANNEL_ID,
            "Statistics Notification",
            NotificationManager.IMPORTANCE_HIGH
        )
        notificationManager.createNotificationChannel(channel)

        val activityIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(context, 0, activityIntent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)

        val yesIntent = Intent(context, StatisticsReceiver::class.java).apply {
            action = context.getString(R.string.yes)
        }
        val yesPendingIntent = PendingIntent.getBroadcast(context, 0, yesIntent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)

        val noIntent = Intent(context, StaticticsReceiver::class.java).apply {
            action = context.getString(R.string.no)
        }
        val noPendingIntent = PendingIntent.getBroadcast(context, 0, noIntent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)

        val builder = NotificationCompat.Builder(context, STATISTICS_CHANNEL_ID)
            .setSmallIcon(R.drawable.alarm_icon)
            .setContentTitle(context.getString(R.string.did_u_pray))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setVibrate(longArrayOf(0, 500, 1000))
            .addAction(R.drawable.check_circle, context.getString(R.string.yes), yesPendingIntent)
            .addAction(R.drawable.cross_icon, context.getString(R.string.no), noPendingIntent)

        notificationManager.notify(context.getString(R.string.did_u_pray).hashCode(),builder.build())
    }

    companion object {
        const val PRAY_CHANNEL_ID = "pray_channel"
        const val STATISTICS_CHANNEL_ID  ="statistics_channel"
    }

    private fun deleteNotificationChannel(context: Context) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.deleteNotificationChannel(PRAY_CHANNEL_ID)

    }
}