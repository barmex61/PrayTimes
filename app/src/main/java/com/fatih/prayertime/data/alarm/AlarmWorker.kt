package com.fatih.prayertime.data.alarm

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.fatih.prayertime.domain.model.Address
import com.fatih.prayertime.domain.model.GlobalAlarm
import com.fatih.prayertime.domain.model.PrayTimes
import com.fatih.prayertime.domain.use_case.alarm_use_cases.get_all_global_alarms_use_case.GetAllGlobalAlarmsUseCase
import com.fatih.prayertime.domain.use_case.formatted_use_cases.formatted_use_case.FormattedUseCase
import com.fatih.prayertime.domain.use_case.location_use_cases.get_last_known_address_from_database_use_case.GetLastKnowAddressFromDatabaseUseCase
import com.fatih.prayertime.domain.use_case.location_use_cases.get_location_and_adress_use_case.GetLocationAndAddressUseCase
import com.fatih.prayertime.domain.use_case.pray_times_use_cases.get_monthly_pray_times_use_case.GetMonthlyPrayTimesFromApiUseCase
import com.fatih.prayertime.domain.use_case.pray_times_use_cases.get_pray_times_at_address_from_database_use_case.GetDailyPrayTimesWithAddressAndDateUseCase
import com.fatih.prayertime.domain.use_case.pray_times_use_cases.insert_pray_time_into_db_use_case.InsertPrayTimeIntoDbUseCase
import com.fatih.prayertime.util.PrayTimesString
import com.fatih.prayertime.util.Status
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.threeten.bp.LocalDate

@AssistedFactory
interface AlarmWorkerFactory {
    fun create(appContext: Context, workerParams: WorkerParameters): AlarmWorker
}

@HiltWorker
class AlarmWorker @AssistedInject constructor(
    @Assisted context : Context,
    @Assisted workerParams : WorkerParameters,
    private val formattedUseCase: FormattedUseCase,
    private val alarmScheduler: AlarmScheduler,
    private val getDailyPrayTimesWithAddressAndDateUseCase: GetDailyPrayTimesWithAddressAndDateUseCase,
    private val getLastKnowAddressFromDatabaseUseCase: GetLastKnowAddressFromDatabaseUseCase,
    private val getAllGlobalAlarmsUseCase: GetAllGlobalAlarmsUseCase,
    private val getMonthlyPrayTimesFromApiUseCase: GetMonthlyPrayTimesFromApiUseCase,
    private val insetPrayTimeIntoDbUseCase: InsertPrayTimeIntoDbUseCase,
    private val getLocationAndAddressUseCase: GetLocationAndAddressUseCase
    ) : Worker(context,workerParams) {

    init {
        println("initialize workmanager")
    }

    override fun doWork(): Result {
        CoroutineScope(Dispatchers.IO + SupervisorJob()).launch {
            println("work")
        }
        return Result.success()
        /*
    override  fun doWork(): Result  {
        CoroutineScope(Dispatchers.IO + SupervisorJob()).launch {

        }
        try {
            println("Started worker")
            // Get current location and address
            val addressResource = getLocationAndAddressUseCase().filter {
                it.status == Status.SUCCESS
            }.firstOrNull()
            val lastKnownAddressDatabase = getLastKnowAddressFromDatabaseUseCase()
            val lastKnownAddress = if (addressResource == null){
                println("if içi")
                lastKnownAddressDatabase ?: return@withContext Result.failure()
            }else{
                println("else içi")
                if (lastKnownAddressDatabase == null) addressResource.data!!
                else addressResource.data!!
            }

            if (addressResource != null && !isAddressesEqual(addressResource.data!!,lastKnownAddressDatabase)) {
                updateTodaysAlarms(addressResource.data)
            }

            // Get current date and time
            val localDateNow = LocalDate.now()
            val formattedDate = formattedUseCase.formatOfPatternDDMMYYYY(localDateNow)
            val currentTimeInMillis = System.currentTimeMillis()

            // Get all global alarms
            val globalAlarms: List<GlobalAlarm> = getAllGlobalAlarmsUseCase().first()

            val newGlobalAlarms = globalAlarms.map { alarm ->
                if (alarm.isEnabled){
                    if (currentTimeInMillis > alarm.alarmTime){
                        val nextDayString = formattedUseCase.formatOfPatternDDMMYYYY(LocalDate.now().plusDays(1))
                        val nextDayPrayTimes = getDailyPrayTimesWithAddressAndDateUseCase(lastKnownAddress, nextDayString)
                        if (nextDayPrayTimes == null){
                            val nextDayLocalDate = formattedUseCase.formatDDMMYYYYDateToLocalDate(nextDayString)
                            val apiResponse = getMonthlyPrayTimesFromApiUseCase(nextDayLocalDate.year, nextDayLocalDate.monthValue, lastKnownAddress)
                            if (apiResponse.status == Status.SUCCESS){
                                insetPrayTimeIntoDbUseCase.insertPrayTimeList(apiResponse.data!!)
                                val updatedNextPrayTime = getDailyPrayTimesWithAddressAndDateUseCase(lastKnownAddress, nextDayString)
                                if (updatedNextPrayTime == null) return@withContext Result.failure()
                                else setNextGlobalAlarm(alarm,updatedNextPrayTime, nextDayString)
                            }else{
                                println("apiResponse.status ${apiResponse.status}")
                                return@withContext Result.failure()
                            }

                        }else{
                            setNextGlobalAlarm(alarm,nextDayPrayTimes,nextDayString)
                        }
                    }else{
                        alarm
                    }
                }
                else{
                    alarm
                }
            }
            println("bugün $formattedDate")
            println("LocaleDate.now() ${LocalDate.now()}")
            println("newGlobalAlarms ${newGlobalAlarms.toTypedArray()}")
            println("globalAlarms ${globalAlarms.toTypedArray()}")
            newGlobalAlarms.forEach { alarm ->
                println("alarm worker working $alarm")
                alarmScheduler.update(alarm)
            }
            return@withContext Result.success()
        } catch (e: Exception) {
            println(e.message)
            return@withContext Result.failure()
        }
    }

    private fun setNextGlobalAlarm(alarm: GlobalAlarm, nextDayPrayTimes : PrayTimes, nextDayString : String) : GlobalAlarm{
        val alarmTime = when(alarm.alarmType){
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
        val (_,_,country1,city1,district1,_,_) = address1
        val (_,_,country2,city2,district2,_,_) = address2
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
}*/
    }
}