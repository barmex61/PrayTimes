package com.fatih.prayertime.data.alarm

import android.content.Context
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
    private val alarmScheduler: AlarmScheduler,
    private val updateGlobalAlarmUseCase: UpdateGlobalAlarmUseCase,
    private val permissionUseCase : PermissionsUseCase
) : CoroutineWorker(context, workerParams) {

    init {
        println("initialize workmanager")
    }

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            println("Started worker")

            // Get current location and address
            val addressResource = if(permissionUseCase.checkLocationPermission()){
                    getLocationAndAddressUseCase().filter {
                        it.status == Status.SUCCESS
                    }.firstOrNull()
                }else null

            val lastKnownAddressDatabase = getLastKnowAddressFromDatabaseUseCase()
            val lastKnownAddress = if (addressResource == null) {
               println("if içi")
                lastKnownAddressDatabase ?: return@withContext Result.failure()
            } else {
                println("else içi")
                if (lastKnownAddressDatabase == null) addressResource.data!!
                else addressResource.data!!
            }

            if (addressResource != null && !isAddressesEqual(addressResource.data!!, lastKnownAddressDatabase)) {
                updateTodaysAlarms(addressResource.data)
            }

            // Get current date and time
            val localDateNow = LocalDate.now()
            val formattedDate = formattedUseCase.formatOfPatternDDMMYYYY(localDateNow)
            val currentTimeInMillis = System.currentTimeMillis()

            // Get all global alarms
            val globalAlarms: List<GlobalAlarm> = getAllGlobalAlarmsUseCase().first()

            val newGlobalAlarms = globalAlarms.map { alarm ->
                if (alarm.isEnabled) {
                    println("alarm is enabled $alarm")
                    if (currentTimeInMillis > alarm.alarmTime) {
                        println("currentTimeInmillis > alarmTime ")
                        val nextDayString = formattedUseCase.formatOfPatternDDMMYYYY(LocalDate.now().plusDays(1))
                        val nextDayPrayTimes = getDailyPrayTimesWithAddressAndDateUseCase(lastKnownAddress, nextDayString)
                        if (nextDayPrayTimes == null) {
                            val nextDayLocalDate = formattedUseCase.formatDDMMYYYYDateToLocalDate(nextDayString)
                            val apiResponse = getMonthlyPrayTimesFromApiUseCase(nextDayLocalDate.year, nextDayLocalDate.monthValue, lastKnownAddress)
                            if (apiResponse.status == Status.SUCCESS) {
                                println("apiResponse SUCCESS")
                                insetPrayTimeIntoDbUseCase.insertPrayTimeList(apiResponse.data!!)
                                val updatedNextPrayTime = getDailyPrayTimesWithAddressAndDateUseCase(lastKnownAddress, nextDayString)
                                if (updatedNextPrayTime == null) return@withContext Result.failure()
                                else setNextGlobalAlarm(alarm, updatedNextPrayTime, nextDayString)
                            } else {
                               println("apiResponse.status ${apiResponse.status}")
                                return@withContext Result.failure()
                            }
                        } else {
                           println("setNextGlobalAlarm")
                            setNextGlobalAlarm(alarm, nextDayPrayTimes, nextDayString)
                        }
                    } else {
                        println("currentAlarmTime < alarmTime $alarm")
                        alarm
                    }
                } else {
                    println("Alarm is not enabled $alarm")
                    alarm
                }
            }
            newGlobalAlarms.forEach { alarm ->
                println("NewGlobalAlarm $alarm")
                updateGlobalAlarmUseCase(alarm)
                alarmScheduler.update(alarm)
            }
            return@withContext Result.success()
        } catch (e: Exception) {
            println("Exception in alarmWorker: ${e.message} ${e.localizedMessage} ${e.stackTrace} ${e.cause?.message}")
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
        val nextLocalDateTime = formattedUseCase.formatDDMMYYYYHHMMDateToLocalDateTime("$nextDayString $alarmTime")
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
                    val todayPrayTimes = getDailyPrayTimesWithAddressAndDateUseCase(newAddress, formattedDate) ?: return@forEach
                    val updatedAlarm = setNextGlobalAlarm(alarm, todayPrayTimes, formattedDate)
                    alarmScheduler.update(updatedAlarm)
                }
            }
        }
    }
}