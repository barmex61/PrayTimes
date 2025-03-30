package com.fatih.prayertime.data.alarm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.fatih.prayertime.R
import com.fatih.prayertime.domain.model.Address
import com.fatih.prayertime.domain.model.PrayerAlarm
import com.fatih.prayertime.domain.use_case.alarm_use_cases.UpdateGlobalAlarmUseCase
import com.fatih.prayertime.domain.use_case.formatted_use_cases.FormattedUseCase
import com.fatih.prayertime.domain.use_case.location_use_cases.GetLastKnowAddressFromDatabaseUseCase
import com.fatih.prayertime.domain.use_case.location_use_cases.GetLocationAndAddressUseCase
import com.fatih.prayertime.domain.use_case.permission_use_case.PermissionsUseCase
import com.fatih.prayertime.domain.use_case.pray_times_use_cases.DeletePrayTimesBeforeDateUseCase
import com.fatih.prayertime.domain.use_case.pray_times_use_cases.GetDailyPrayTimesWithAddressAndDateUseCase
import com.fatih.prayertime.domain.use_case.pray_times_use_cases.GetMonthlyPrayTimesFromApiUseCase
import com.fatih.prayertime.domain.use_case.pray_times_use_cases.InsertPrayTimeIntoDbUseCase
import com.fatih.prayertime.domain.use_case.alarm_use_cases.GetAllGlobalAlarmsUseCase
import com.fatih.prayertime.util.model.state.Status
import com.fatih.prayertime.util.utils.AlarmUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.threeten.bp.LocalDate
import javax.inject.Inject

@AndroidEntryPoint
class LocationUpdateForegroundService : Service() {
    
    private val NOTIFICATION_ID = 1001
    private val CHANNEL_ID = "location_update_channel"
    private val TAG = "LocationUpdateService"
    
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    
    @Inject
    lateinit var getLocationAndAddressUseCase: GetLocationAndAddressUseCase
    
    @Inject
    lateinit var getLastKnowAddressFromDatabaseUseCase: GetLastKnowAddressFromDatabaseUseCase
    
    @Inject
    lateinit var formattedUseCase: FormattedUseCase
    
    @Inject
    lateinit var getAllGlobalAlarmsUseCase: GetAllGlobalAlarmsUseCase
    
    @Inject
    lateinit var getDailyPrayTimesWithAddressAndDateUseCase: GetDailyPrayTimesWithAddressAndDateUseCase
    
    @Inject
    lateinit var getMonthlyPrayTimesFromApiUseCase: GetMonthlyPrayTimesFromApiUseCase
    
    @Inject
    lateinit var insertPrayTimeIntoDbUseCase: InsertPrayTimeIntoDbUseCase
    
    @Inject
    lateinit var updateGlobalAlarmUseCase: UpdateGlobalAlarmUseCase
    
    @Inject
    lateinit var permissionUseCase: PermissionsUseCase
    
    @Inject
    lateinit var deletePrayTimesBeforeDateUseCase: DeletePrayTimesBeforeDateUseCase
    
    override fun onBind(intent: Intent?): IBinder? = null
    
    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notificationTitle = getString(R.string.app_name)
        val notificationText = getString(R.string.pray_notification_channel_description)
        
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(notificationTitle)
            .setContentText(notificationText)
            .setSmallIcon(R.drawable.baseline_notifications_24)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
        
        startForeground(NOTIFICATION_ID, notification)
        
        serviceScope.launch {
            try {
                updatePrayerTimes()
            } catch (e: Exception) {
                Log.e(TAG, "Error updating prayer times", e)
            } finally {
                stopSelf() 
            }
        }
        
        return START_NOT_STICKY
    }
    
    private suspend fun updatePrayerTimes() = withContext(Dispatchers.IO) {
        try {
            val addressResource = if(permissionUseCase.checkLocationPermission()){
                getLocationAndAddressUseCase().filter {
                    it.status == Status.SUCCESS
                }.firstOrNull()
            } else null
            
            Log.d(TAG,"Address resource : $addressResource")
            val lastKnownAddressDatabase = getLastKnowAddressFromDatabaseUseCase()
            val lastKnownAddress = if (addressResource == null) {
                lastKnownAddressDatabase ?: return@withContext
            } else {
                if (lastKnownAddressDatabase == null) addressResource.data!!
                else addressResource.data!!
            }

            if (addressResource != null && !isAddressesEqual(addressResource.data!!, lastKnownAddressDatabase)) {
                updateMonthlyPrayTimes(addressResource.data)
            }
            val newGlobalAlarms = getNewGlobalAlarms(lastKnownAddress)
            newGlobalAlarms.forEach { alarm ->
                Log.d(TAG,"New GlobalAlarm $alarm")
                updateGlobalAlarmUseCase(alarm)
            }
        } catch (e: Exception) {
            Log.e(TAG,"Exception in LocationUpdateService: ${e.message}", e)
        }
    }
    
    private suspend fun getNewGlobalAlarms(lastKnownAddress: Address) : List<PrayerAlarm> {
        val prayerAlarms: List<PrayerAlarm> = getAllGlobalAlarmsUseCase().first()
        val localDateNow = LocalDate.now()
        val localDateString = formattedUseCase.formatOfPatternDDMMYYYY(localDateNow)
        val localeDateLong = formattedUseCase.formatLocalDateToLong(localDateNow)
        deletePrayTimesBeforeDateUseCase(localeDateLong)
        return prayerAlarms.map { alarm ->
            if (alarm.isEnabled){
                //Alarm enabled
                val currentTime = System.currentTimeMillis()
                val alarmTimeInMillis = alarm.alarmTime
                if (currentTime > alarmTimeInMillis){
                    //Current time is greater than alarm time
                    Log.d(TAG,"Alarm already passed . New alarm setting for next day")
                    val nextDayString = formattedUseCase.formatOfPatternDDMMYYYY(localDateNow.plusDays(1))
                    val nextDayPrayTimes = getDailyPrayTimesWithAddressAndDateUseCase(lastKnownAddress, nextDayString)
                    if (nextDayPrayTimes == null){
                        //Next day pray times is null
                        val nextDayLocalDate = formattedUseCase.formatDDMMYYYYDateToLocalDate(nextDayString)
                        val apiResponse = getMonthlyPrayTimesFromApiUseCase(nextDayLocalDate.year, nextDayLocalDate.monthValue, lastKnownAddress)
                        if (apiResponse.status == Status.SUCCESS){
                            insertPrayTimeIntoDbUseCase.insertPrayTimeList(apiResponse.data!!)
                            val updatedNextPrayTime = getDailyPrayTimesWithAddressAndDateUseCase(lastKnownAddress, nextDayString)
                            if (updatedNextPrayTime == null) return@map alarm
                            setGlobalAlarm(alarm, updatedNextPrayTime, nextDayString)
                        }else{
                            alarm
                        }
                    }else{
                        setGlobalAlarm(alarm, nextDayPrayTimes, nextDayString)
                    }
                }else{
                    Log.d(TAG,"Alarm not triggered yet .There is no change for it $alarm")
                    alarm
                }
            }else{
                //Alarm not enabled
                Log.d(TAG,"Alarm is not enabled $alarm")
                alarm
            }
        }
    }

    private fun setGlobalAlarm(alarm: PrayerAlarm, prayTimes: com.fatih.prayertime.domain.model.PrayTimes, localDateString: String): PrayerAlarm {
        val prayTime = AlarmUtils.getPrayTimeForPrayType(prayTimes, alarm.alarmType, alarm.alarmOffset, formattedUseCase)
        val prayTimeInMillis = formattedUseCase.formatHHMMtoLongWithLocalDate(prayTime, formattedUseCase.formatDDMMYYYYDateToLocalDate(localDateString))
        val prayTimeString = formattedUseCase.formatLongToLocalDateTime(prayTimeInMillis)
        return alarm.copy(alarmTime = prayTimeInMillis, alarmTimeString = prayTimeString)
    }

    private fun isAddressesEqual(address1: Address, address2: Address?): Boolean {
        if (address2 == null) return false
        val (_, _, country1, city1, district1, _, _) = address1
        val (_, _, country2, city2, district2, _, _) = address2
        return country1 == country2 && city1 == city2 && district1 == district2
    }

    private suspend fun updateMonthlyPrayTimes(newAddress: Address) {
        val localDateNow = LocalDate.now()
        val year = localDateNow.year
        val month = localDateNow.monthValue
        val apiResponse = getMonthlyPrayTimesFromApiUseCase(year, month, newAddress)
        if (apiResponse.status == Status.SUCCESS) {
            insertPrayTimeIntoDbUseCase.insertPrayTimeList(apiResponse.data!!)
        }
    }
    
    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            getString(R.string.pray_notification_channel_name),
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = getString(R.string.pray_notification_channel_description)
        }
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
    
    override fun onDestroy() {
        serviceScope.cancel()
        super.onDestroy()
    }
} 