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
import androidx.core.app.NotificationCompat
import androidx.core.net.toUri
import com.fatih.prayertime.R
import com.fatih.prayertime.presentation.main_activity.MainActivity
import com.fatih.prayertime.util.model.enums.AlarmType
import com.fatih.prayertime.util.utils.AlarmUtils.getContentTitleForPrayType

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val alarmType = intent.getStringExtra("ALARM_TYPE")
        Log.d("AlarmReceiver","ss")

        when(alarmType){
            AlarmType.PRAY.name ->{
                val prayAlarmType = intent.getStringExtra("PRAY_TYPE") ?: "Bilinmeyen Alarm"
                val enableVibration = intent.getBooleanExtra("VIBRATION", true)
                val isSilent = intent.getBooleanExtra("IS_SILENT", false)
                val alarmSoundUri =
                    if(isSilent) RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                    else intent.getStringExtra("SOUND_URI")?.toUri() ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
                showNotificationForPray(context, prayAlarmType, enableVibration, alarmSoundUri)
            }
            AlarmType.STATISTICS.name ->{
                Log.d("AlarmReceiver","ss")
                val statsAlarmType = intent.getStringExtra("PRAY_TYPE") ?: "Bilinmeyen Alarm"
                val statAlarmDate = intent.getStringExtra("ALARM_DATE")?:""
                showNotificationForStatistics(context,statsAlarmType,statAlarmDate)
            }
        }
    }

    private fun showNotificationForPray(context: Context, prayType: String, enableVibration : Boolean, alarmSoundUri : Uri) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        deleteNotificationChannel(context,PRAY_CHANNEL_ID)
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
            .setContentTitle(prayType)
            .setContentText(context.getString(R.string.pray_alarm_message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setSound(alarmSoundUri)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setVibrate(vibrationPattern)
        notificationManager.notify(prayType.hashCode(),builder.build())
    }

    private fun showNotificationForStatistics(context: Context, prayType: String, alarmDate : String){
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        deleteNotificationChannel(context,STATISTICS_CHANNEL_ID)
        val channel = NotificationChannel(
            STATISTICS_CHANNEL_ID,
            "Statistics Notification",
            NotificationManager.IMPORTANCE_HIGH
        )
        notificationManager.createNotificationChannel(channel)
        val contentTitle = getContentTitleForPrayType(prayType,context)
        val notificationId = contentTitle.hashCode()
        val activityIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(context, 0, activityIntent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)

        val yesIntent = Intent(context, StatisticsReceiver::class.java).apply {
            action = context.getString(R.string.yes)
            putExtra("PRAY_TYPE",prayType)
            putExtra("ALARM_DATE",alarmDate)
            putExtra("NOTIFICATION_ID",notificationId)
        }
        val yesPendingIntent = PendingIntent.getBroadcast(context, notificationId, yesIntent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)

        val noIntent = Intent(context, StatisticsReceiver::class.java).apply {
            action = context.getString(R.string.no)
            putExtra("PRAY_TYPE",prayType)
            putExtra("ALARM_DATE",alarmDate)
            putExtra("NOTIFICATION_ID",notificationId)
        }

        val noPendingIntent = PendingIntent.getBroadcast(context, notificationId, noIntent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
        val builder = NotificationCompat.Builder(context, STATISTICS_CHANNEL_ID)
            .setSmallIcon(R.drawable.alarm_icon)
            .setContentTitle(contentTitle)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setVibrate(longArrayOf(0, 500, 1000))
            .addAction(R.drawable.check_circle, context.getString(R.string.yes), yesPendingIntent)
            .addAction(R.drawable.cross_icon, context.getString(R.string.no), noPendingIntent)
            .setDeleteIntent(noPendingIntent)


        notificationManager.notify(notificationId,builder.build())
    }

    companion object {
        const val PRAY_CHANNEL_ID = "pray_channel"
        const val STATISTICS_CHANNEL_ID  ="statistics_channel"
    }

    private fun deleteNotificationChannel(context: Context,channelId : String) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.deleteNotificationChannel(channelId)

    }
}