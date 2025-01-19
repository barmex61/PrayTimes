package com.fatih.prayertime.presentation.widget

import android.app.AlarmManager
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.RemoteViews
import com.fatih.prayertime.R
import com.fatih.prayertime.domain.use_case.formatted_use_cases.formatted_use_case.FormattedUseCase
import com.fatih.prayertime.domain.use_case.location_use_cases.get_last_known_address_from_database_use_case.GetLastKnowAddressFromDatabaseUseCase
import com.fatih.prayertime.domain.use_case.pray_times_use_cases.get_pray_times_at_address_from_database_use_case.GetDailyPrayTimesWithAddressAndDateUseCase
import com.fatih.prayertime.util.convertTimeToSeconds
import com.fatih.prayertime.util.toList
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class MyWidgetProvider() : AppWidgetProvider() {

    @Inject
    lateinit var getDailyPrayTimesWithAddressAndDateUseCase: GetDailyPrayTimesWithAddressAndDateUseCase
    @Inject
    lateinit var getLastKnowAddressFromDatabaseUseCase: GetLastKnowAddressFromDatabaseUseCase
    @Inject
    lateinit var formattedUseCase: FormattedUseCase

    private val coroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (AppWidgetManager.ACTION_APPWIDGET_UPDATE == intent.action) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val appWidgetIds = appWidgetManager.getAppWidgetIds(intent.component)
            onUpdate(context, appWidgetManager, appWidgetIds)
        }
    }

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        for (appWidgetId in appWidgetIds) {
            coroutineScope.launch {
                updateAppWidget(context, appWidgetManager, appWidgetId)
            }
        }
        setAlarm(context)
    }

    override fun onDisabled(context: Context) {
        super.onDisabled(context)
        cancelAlarm(context)
        coroutineScope.cancel()
    }

    override fun onAppWidgetOptionsChanged(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int, newOptions: Bundle) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions)
        coroutineScope.launch {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    private suspend fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
        val localDateNow = LocalDateTime.now()
        val formattedDayString = formattedUseCase.formatOfPatternEEEE(localDateNow.toLocalDate())
        val formattedTimeString = formattedUseCase.formatHHMM(localDateNow)
        val formattedDateString = formattedUseCase.formatOfPatternEEE(localDateNow.toLocalDate())
        val searchDateString = formattedUseCase.formatOfPatternDDMMYYYY(localDateNow.toLocalDate())

        val address = getLastKnowAddressFromDatabaseUseCase()
        val prayTimes = if (address != null) getDailyPrayTimesWithAddressAndDateUseCase(address, searchDateString) else null
        val prayTimesList = prayTimes?.toList()?.map { it.second } ?: listOf()
        val nextPrayTime = prayTimesList.firstOrNull { it.convertTimeToSeconds() > formattedTimeString.convertTimeToSeconds() }
        val timeDifference = nextPrayTime?.convertTimeToSeconds()?.minus(formattedTimeString.convertTimeToSeconds()) ?: 0
        val timeDiffAtMinute = timeDifference / 60


        withContext(Dispatchers.Main) {
            val views = RemoteViews(context.packageName, R.layout.widget_layout).apply {
                setTextViewText(R.id.dayTextView,formattedDayString)
                setTextViewText(R.id.timeTextView, formattedTimeString)
                setTextViewText(R.id.dateTextView, formattedDateString)
                setTextViewText(R.id.remainingTimeTextView, "Sonraki vakte kalan süre $timeDiffAtMinute dakika")
            }
            val options = appWidgetManager.getAppWidgetOptions(appWidgetId)
            val minHeight = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT)
            views.setTextViewTextSize(R.id.dayTextView, 2, minHeight / 9f)
            views.setTextViewTextSize(R.id.timeTextView, 2, minHeight / 3f)
            views.setTextViewTextSize(R.id.dateTextView, 2, minHeight / 9f)
            views.setTextViewTextSize(R.id.remainingTimeTextView, 2, minHeight / 11f)
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }

    private fun setAlarm(context: Context) {
        val intent = Intent(context, MyWidgetProvider::class.java).apply {
            action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
        }
        val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setRepeating(AlarmManager.RTC, System.currentTimeMillis(), 60000L, pendingIntent)
    }

    private fun cancelAlarm(context: Context) {
        val intent = Intent(context, MyWidgetProvider::class.java).apply {
            action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
        }
        val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(pendingIntent)
    }
}