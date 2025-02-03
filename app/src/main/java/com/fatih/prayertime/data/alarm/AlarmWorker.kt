package com.fatih.prayertime.data.alarm

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.fatih.prayertime.domain.model.Address
import com.fatih.prayertime.domain.model.GlobalAlarm
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
import com.fatih.prayertime.util.PrayTimesString
import com.fatih.prayertime.util.Status
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import org.threeten.bp.LocalDate


@HiltWorker
class AlarmWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val getLocationAndAddressUseCase: GetLocationAndAddressUseCase,
    private val getLastKnowAddressFromDatabaseUseCase: GetLastKnowAddressFromDatabaseUseCase,
    private val formattedUseCase: FormattedUseCase,
    private val getAllGlobalAlarmsUseCase: GetAllGlobalAlarmsUseCase,
    private val getDailyPrayTimesWithAddressAndDateUseCase: GetDailyPrayTimesWithAddressAndDateUseCase,
    private val getMonthlyPrayTimesFromApiUseCase: GetMonthlyPrayTimesFromApiUseCase,
    private val insetPrayTimeIntoDbUseCase: InsertPrayTimeIntoDbUseCase,
    private val updateGlobalAlarmUseCase: UpdateGlobalAlarmUseCase,
    private val permissionUseCase : PermissionsUseCase,
    private val deletePrayTimesBeforeDateUseCase: DeletePrayTimesBeforeDateUseCase
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
                updateTodaysAlarms(addressResource.data)
            }

            // Get current date and time
            val localDateNow = LocalDate.now()
            val formattedDate = formattedUseCase.formatOfPatternDDMMYYYY(localDateNow)
            val formattedDateLong = formattedUseCase.formatLocalDateToLong(localDateNow)
            deletePrayTimesBeforeDateUseCase(formattedDateLong)
            val currentTimeInMillis = System.currentTimeMillis()

            // Get all global alarms
            val globalAlarms: List<GlobalAlarm> = getAllGlobalAlarmsUseCase().first()

            val newGlobalAlarms = globalAlarms.map { alarm ->
                if (alarm.isEnabled) {
                    Log.d(TAG,"Alarm is enabled $alarm")
                    if (currentTimeInMillis > alarm.alarmTime) {
                        Log.d(TAG,"currentTimeInmillis > alarmTime ")
                        val nextDayString = formattedUseCase.formatOfPatternDDMMYYYY(LocalDate.now().plusDays(1))
                        val nextDayPrayTimes = getDailyPrayTimesWithAddressAndDateUseCase(lastKnownAddress, nextDayString).first()
                        if (nextDayPrayTimes == null) {
                            Log.d(TAG,"Next day pray times is null")
                            val nextDayLocalDate = formattedUseCase.formatDDMMYYYYDateToLocalDate(nextDayString)
                            val apiResponse = getMonthlyPrayTimesFromApiUseCase(nextDayLocalDate.year, nextDayLocalDate.monthValue, lastKnownAddress)
                            if (apiResponse.status == Status.SUCCESS) {
                                Log.d(TAG,"ApiResponse $apiResponse")
                                insetPrayTimeIntoDbUseCase.insertPrayTimeList(apiResponse.data!!)
                                val updatedNextPrayTime = getDailyPrayTimesWithAddressAndDateUseCase(lastKnownAddress, nextDayString).first()
                                if (updatedNextPrayTime == null) return@withContext Result.failure()
                                else setNextGlobalAlarm(alarm, updatedNextPrayTime, nextDayString)
                            } else {
                                Log.d(TAG,"ApiResponse ${apiResponse.status}")
                                return@withContext Result.failure()
                            }
                        } else {
                            Log.d(TAG,"Next day pray times is not null")
                            setNextGlobalAlarm(alarm, nextDayPrayTimes, nextDayString)
                        }
                    } else {
                        Log.d(TAG,"currentAlarmTime < alarmTime $alarm")
                        alarm
                    }
                } else {
                    Log.d(TAG,"Alarm is not enabled $alarm")
                    alarm
                }
            }
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

    private fun setNextGlobalAlarm(alarm: GlobalAlarm, nextDayPrayTimes: PrayTimes, nextDayString: String): GlobalAlarm {
        val alarmTime = when (alarm.alarmType) {
            PrayTimesString.Morning.name -> nextDayPrayTimes.morning
            PrayTimesString.Noon.name -> nextDayPrayTimes.noon
            PrayTimesString.Afternoon.name -> nextDayPrayTimes.afternoon
            PrayTimesString.Evening.name -> nextDayPrayTimes.evening
            PrayTimesString.Night.name -> nextDayPrayTimes.night
            else -> "00:00"
        }
        val nextLocalDateTime = formattedUseCase.formatDDMMYYYYHHMMDateToLocalDateTime("$nextDayString $alarmTime:00")
        val nextPrayTimeInMillis = formattedUseCase.formatLocalDateTimeToLong(nextLocalDateTime)
        val alarmTimeInMillis = nextPrayTimeInMillis + alarm.alarmOffset
        val alarmTimeString = formattedUseCase.formatLongToLocalDateTime(alarmTimeInMillis)
        return alarm.copy(alarmTime = alarmTimeInMillis, alarmTimeString = alarmTimeString)
    }

    private fun isAddressesEqual(address1: Address, address2: Address?): Boolean {
        if (address2 == null) return false
        val (_, _, country1, city1, district1, _, _) = address1
        val (_, _, country2, city2, district2, _, _) = address2
        return country1 == country2 && city1 == city2 && district1 == district2
    }

    private suspend fun updateTodaysAlarms(newAddress: Address) {
        val localDateNow = LocalDate.now()
        val monthlyApiResponse = getMonthlyPrayTimesFromApiUseCase(localDateNow.year, localDateNow.monthValue, newAddress)
        if (monthlyApiResponse.status == Status.SUCCESS) {
            insetPrayTimeIntoDbUseCase.insertPrayTimeList(monthlyApiResponse.data!!)
            val globalAlarms: List<GlobalAlarm> = getAllGlobalAlarmsUseCase().first()
            globalAlarms.forEach { alarm ->
                if (alarm.isEnabled && System.currentTimeMillis() < alarm.alarmTime) {
                    val formattedDate = formattedUseCase.formatOfPatternDDMMYYYY(localDateNow)
                    val todayPrayTimes = getDailyPrayTimesWithAddressAndDateUseCase(newAddress, formattedDate) .first()?: return@forEach
                    val updatedAlarm = setNextGlobalAlarm(alarm, todayPrayTimes, formattedDate)
                    updateGlobalAlarmUseCase(updatedAlarm)
                }
            }
        }
    }
}