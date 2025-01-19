package com.fatih.prayertime.presentation.main_screen.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fatih.prayertime.domain.model.GlobalAlarm
import com.fatih.prayertime.domain.model.Address
import com.fatih.prayertime.domain.model.PrayTimes
import com.fatih.prayertime.domain.use_case.formatted_use_cases.formatted_use_case.FormattedUseCase
import com.fatih.prayertime.domain.use_case.alarm_use_cases.get_all_global_alarms_use_case.GetAllGlobalAlarmsUseCase
import com.fatih.prayertime.domain.use_case.pray_times_use_cases.get_monthly_pray_times_use_case.GetMonthlyPrayTimesFromApiUseCase
import com.fatih.prayertime.domain.use_case.alarm_use_cases.get_global_alarm_by_type_use_case.GetGlobalAlarmByTypeUseCase
import com.fatih.prayertime.domain.use_case.location_use_cases.get_last_known_address_from_database_use_case.GetLastKnowAddressFromDatabaseUseCase
import com.fatih.prayertime.domain.use_case.location_use_cases.get_location_and_adress_use_case.GetLocationAndAddressUseCase
import com.fatih.prayertime.domain.use_case.pray_times_use_cases.get_pray_times_at_address_from_database_use_case.GetDailyPrayTimesWithAddressAndDateUseCase
import com.fatih.prayertime.domain.use_case.alarm_use_cases.insert_global_alarm_use_case.InsertGlobalAlarmUseCase
import com.fatih.prayertime.domain.use_case.pray_times_use_cases.insert_pray_time_into_db_use_case.InsertPrayTimeIntoDbUseCase
import com.fatih.prayertime.domain.use_case.alarm_use_cases.update_global_alarm_use_case.UpdateGlobalAlarmUseCase
import com.fatih.prayertime.util.PrayTimesString
import com.fatih.prayertime.util.Resource
import com.fatih.prayertime.util.Status
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import org.threeten.bp.Year
import org.threeten.bp.YearMonth
import javax.inject.Inject

@HiltViewModel
class MainScreenViewModel @Inject constructor(
    private val getMonthlyPrayTimesFromApiUseCase: GetMonthlyPrayTimesFromApiUseCase,
    private val getLocationAndAddressUseCase: GetLocationAndAddressUseCase,
    private val formattedUseCase: FormattedUseCase,
    private val getDailyPrayTimesWithAddressAndDateUseCase: GetDailyPrayTimesWithAddressAndDateUseCase,
    private val insertPrayTimeIntoDbUseCase : InsertPrayTimeIntoDbUseCase,
    private val getLastKnownAddressFromDatabaseUseCase: GetLastKnowAddressFromDatabaseUseCase,
    private val getAllGlobalAlarmsUseCase: GetAllGlobalAlarmsUseCase,
    private val getGlobalAlarmByTypeUseCase: GetGlobalAlarmByTypeUseCase,
    private val insertGlobalAlarmUseCase : InsertGlobalAlarmUseCase,
    private val updateGlobalAlarmUseCase: UpdateGlobalAlarmUseCase,
) : ViewModel() {

    //Pray - Times

    private val _dailyPrayTimes = MutableStateFlow<Resource<PrayTimes>>(Resource.loading())
    val dailyPrayTimes: StateFlow<Resource<PrayTimes>> = _dailyPrayTimes

    fun trackLocationAndUpdatePrayTimes() = viewModelScope.launch(Dispatchers.IO) {
        getLocationAndAddressUseCase().collect { resource ->
            when(resource.status){
                Status.SUCCESS -> {
                    getMonthlyPrayTimesFromAPI(Year.now().value, YearMonth.now().monthValue,resource.data!!)
                }
                Status.ERROR ->{
                    getDailyPrayTimesFromDb()
                }
                else -> Unit
            }
        }
    }

    fun getMonthlyPrayTimesFromAPI(year: Int,month : Int , address: Address?) = viewModelScope.launch(Dispatchers.Default) {
        val searchAddress = address?:getLastKnownAddressFromDatabaseUseCase()?: return@launch
        val databaseResponse = getDailyPrayTimesWithAddressAndDateUseCase(searchAddress,formattedDate.value)
        if (databaseResponse != null) {
            _dailyPrayTimes.emit(Resource.success(databaseResponse))
            return@launch
        }
        val apiResponse = getMonthlyPrayTimesFromApiUseCase(year ,month,searchAddress)
        if (apiResponse.status == Status.SUCCESS){
            insertPrayTimeIntoDbUseCase.insertPrayTimeList(apiResponse.data!!)
            getDailyPrayTimesFromDb()
        }
    }

    fun getDailyPrayTimesFromDb() = viewModelScope.launch(Dispatchers.Default){
        val searchAddress = getLastKnownAddressFromDatabaseUseCase() ?: return@launch
        val databaseResponse = getDailyPrayTimesWithAddressAndDateUseCase(searchAddress, formattedDate.value )
        if (databaseResponse != null)  _dailyPrayTimes.emit(Resource.success(databaseResponse))
    }


    //Date

    private val _formattedDate : MutableStateFlow<String> = MutableStateFlow("")
    val formattedDate : StateFlow<String> = _formattedDate

    fun updateFormattedDate() = viewModelScope.launch(Dispatchers.Default){
        _formattedDate.emit(formattedUseCase.formatOfPatternDDMMYYYY(LocalDate.now()))
    }

    private val _formattedTime : MutableStateFlow<String> = MutableStateFlow("")
    val formattedTime : StateFlow<String> = _formattedTime

    fun updateFormattedTime() = viewModelScope.launch(Dispatchers.Default){
        _formattedTime.emit(formattedUseCase.formatHHMMSS(LocalDateTime.now()))
    }

   // Alarm--

    private val _globalAlarmList : MutableStateFlow<List<GlobalAlarm>?> = MutableStateFlow(null)
    val globalAlarmList : StateFlow<List<GlobalAlarm>?> = _globalAlarmList

    private val _globalAlarm = MutableStateFlow<GlobalAlarm?>(null)

    private val _selectedGlobalAlarm = MutableStateFlow<GlobalAlarm?>(null)
    val selectedGlobalAlarm : StateFlow<GlobalAlarm?> = _selectedGlobalAlarm

    fun setSelectedGlobalAlarm(globalAlarm: GlobalAlarm) {
        _selectedGlobalAlarm.value = globalAlarm
    }

    fun updateGlobalAlarm(
        alarmType : String,
        alarmTimeLong: Long,
        alarmTimeString : String,
        isEnabled: Boolean,
        alarmOffset: Long
    ) = viewModelScope.launch(Dispatchers.Default){

        try {
            val globalAlarm = GlobalAlarm(alarmType,alarmTimeLong,alarmTimeString,isEnabled,alarmOffset)
            updateGlobalAlarmUseCase(globalAlarm)
        }catch (e:Exception){
            println(e.message)
        }
    }

    fun getGlobalAlarm(alarmType: String) = viewModelScope.launch(Dispatchers.Default){
        try {
            _globalAlarm.emit(getGlobalAlarmByTypeUseCase(alarmType))
        }catch (e:Exception){
            println(e.message)
        }
    }

    private fun initAndSetGlobalAlarmList() =  viewModelScope.launch(Dispatchers.Default){
        try {
            getAllGlobalAlarmsUseCase().collect { globalAlarmList ->
                if (globalAlarmList.isEmpty()) {
                    val initialAlarms = PrayTimesString.entries.map {
                        GlobalAlarm(
                            alarmType = it.name,
                            alarmTime = 0L,
                            alarmTimeString = "16-01-2025 00:00",
                            isEnabled = false,
                            alarmOffset = 0
                        )
                    }

                    initialAlarms.forEach { globalAlarms ->
                        insertGlobalAlarmUseCase(globalAlarms)
                    }
                } else {
                    _globalAlarmList.emit(globalAlarmList)
                }
            }
        }catch (e:Exception){
            println("catch message ${e.message}")
        }
    }

    fun getHourAndMinuteFromIndex(index: Int) : Pair<Int,Int> {
        _dailyPrayTimes.value.data?:return Pair(0,0)
        val prayTimes = _dailyPrayTimes.value.data!!
        val timeString = when(index){
            0 -> prayTimes.morning
            1 -> prayTimes.noon
            2 -> prayTimes.afternoon
            3 -> prayTimes.evening
            else -> prayTimes.night
        }
        try {
            val splitList = timeString.split(":")
            val hour = splitList.first().toInt()
            val minutes = splitList[1].toInt()
            return Pair(hour, minutes)
        }catch (e:Exception){
            println(e.message)
            return Pair(0,0)
        }
    }


    init {
        updateFormattedDate()
        updateFormattedTime()
        initAndSetGlobalAlarmList()
    }


}