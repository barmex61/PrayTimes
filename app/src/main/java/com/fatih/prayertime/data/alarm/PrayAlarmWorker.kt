package com.fatih.prayertime.data.alarm

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.fatih.prayertime.data.di.WorkerLocation
import com.fatih.prayertime.domain.model.Address
import com.fatih.prayertime.domain.model.PrayerAlarm
import com.fatih.prayertime.domain.model.PrayTimes
import com.fatih.prayertime.domain.use_case.alarm_use_cases.GetAllGlobalAlarmsUseCase
import com.fatih.prayertime.domain.use_case.alarm_use_cases.UpdateGlobalAlarmUseCase
import com.fatih.prayertime.domain.use_case.formatted_use_cases.FormattedUseCase
import com.fatih.prayertime.domain.use_case.location_use_cases.GetLastKnowAddressFromDatabaseUseCase
import com.fatih.prayertime.domain.use_case.location_use_cases.GetLocationAndAddressUseCase
import com.fatih.prayertime.domain.use_case.permission_use_case.PermissionsUseCase
import com.fatih.prayertime.domain.use_case.pray_times_use_cases.DeletePrayTimesBeforeDateUseCase
import com.fatih.prayertime.domain.use_case.pray_times_use_cases.GetMonthlyPrayTimesFromApiUseCase
import com.fatih.prayertime.domain.use_case.pray_times_use_cases.GetDailyPrayTimesWithAddressAndDateUseCase
import com.fatih.prayertime.domain.use_case.pray_times_use_cases.InsertPrayTimeIntoDbUseCase
import com.fatih.prayertime.util.model.state.Status
import com.fatih.prayertime.util.utils.AlarmUtils.getPrayTimeForPrayType
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import org.threeten.bp.LocalDate


@HiltWorker
class PrayAlarmWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    @WorkerLocation private val getLocationAndAddressUseCase: GetLocationAndAddressUseCase,
    private val getLastKnowAddressFromDatabaseUseCase: GetLastKnowAddressFromDatabaseUseCase,
    private val formattedUseCase: FormattedUseCase,
    private val getAllGlobalAlarmsUseCase: GetAllGlobalAlarmsUseCase,
    private val getDailyPrayTimesWithAddressAndDateUseCase: GetDailyPrayTimesWithAddressAndDateUseCase,
    private val getMonthlyPrayTimesFromApiUseCase: GetMonthlyPrayTimesFromApiUseCase,
    private val insetPrayTimeIntoDbUseCase: InsertPrayTimeIntoDbUseCase,
    private val updateGlobalAlarmUseCase: UpdateGlobalAlarmUseCase,
    private val permissionUseCase : PermissionsUseCase,
    private val deletePrayTimesBeforeDateUseCase: DeletePrayTimesBeforeDateUseCase,
) : CoroutineWorker(context, workerParams) {


    companion object{
        const val TAG = "AlarmWorker"
    }
    init {
        Log.d(TAG,"İnitialized")
    }

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG,"Started")
            // Get current location and address
            val addressResource = if(permissionUseCase.checkLocationPermission()){
                    getLocationAndAddressUseCase().filter {
                        it.status == Status.SUCCESS
                    }.firstOrNull()
                }else null
            Log.d(TAG,"Address resource : $addressResource")
            val lastKnownAddressDatabase = getLastKnowAddressFromDatabaseUseCase()
            val lastKnownAddress = if (addressResource == null) {
                lastKnownAddressDatabase ?: return@withContext Result.failure()
            } else {
                if (lastKnownAddressDatabase == null) addressResource.data!!
                else addressResource.data!!
            }

            if (addressResource != null && !isAddressesEqual(addressResource.data!!, lastKnownAddressDatabase)) {
                updateMonthlyPrayTimes(addressResource.data)
            }
            // Get all global alarms
            val newGlobalAlarms = getNewGlobalAlarms(lastKnownAddress)
            newGlobalAlarms.forEach { alarm ->
                Log.d(TAG,"New GlobalAlarm $alarm")
                updateGlobalAlarmUseCase(alarm)
            }
            return@withContext Result.success()
        } catch (e: Exception) {
            Log.d(TAG,"Exception in alarmWorker: ${e.message} ${e.localizedMessage} ${e.stackTrace} ${e.cause?.message}")
            return@withContext Result.failure()
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
                val prayTime = getDailyPrayTimesWithAddressAndDateUseCase(lastKnownAddress,localDateString) ?: return@map alarm
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
                            insetPrayTimeIntoDbUseCase.insertPrayTimeList(apiResponse.data!!)
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

    private fun setGlobalAlarm(alarm: PrayerAlarm, prayTimes: PrayTimes, localDateString: String): PrayerAlarm {
        val prayTime = getPrayTimeForPrayType(prayTimes, alarm.alarmType, alarm.alarmOffset,formattedUseCase)
        val prayTimeInMillis = formattedUseCase.formatHHMMtoLongWithLocalDate(prayTime,formattedUseCase.formatDDMMYYYYDateToLocalDate(localDateString))
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
            insetPrayTimeIntoDbUseCase.insertPrayTimeList(apiResponse.data!!)
        }
    }
}