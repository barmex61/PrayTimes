package com.fatih.prayertime.presentation.main_screen.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fatih.prayertime.domain.model.GlobalAlarm
import com.fatih.prayertime.domain.model.Address
import com.fatih.prayertime.domain.model.PrayTimes
import com.fatih.prayertime.domain.use_case.formatted_use_cases.formatted_date_use_case.FormattedDateUseCase
import com.fatih.prayertime.domain.use_case.formatted_use_cases.formatted_time_use_case.FormattedTimeUseCase
import com.fatih.prayertime.domain.use_case.alarm_use_cases.get_all_global_alarms_use_case.GetAllGlobalAlarmsUseCase
import com.fatih.prayertime.domain.use_case.pray_times_use_cases.get_daily_pray_times_use_case.GetDailyPrayTimesFromApiUseCase
import com.fatih.prayertime.domain.use_case.alarm_use_cases.get_global_alarm_by_type_use_case.GetGlobalAlarmByTypeUseCase
import com.fatih.prayertime.domain.use_case.formatted_use_cases.get_hour_and_minute_from_long.GetHourAndMinuteFromLongUseCase
import com.fatih.prayertime.domain.use_case.location_use_cases.get_last_known_address_from_database_use_case.GetLastKnowAddressFromDatabaseUseCase
import com.fatih.prayertime.domain.use_case.location_use_cases.get_location_and_adress_use_case.GetLocationAndAddressUseCase
import com.fatih.prayertime.domain.use_case.pray_times_use_cases.get_pray_times_at_address_from_database_use_case.GetDailyPrayTimesAtAddressFromDatabaseUseCase
import com.fatih.prayertime.domain.use_case.alarm_use_cases.insert_global_alarm_use_case.InsertGlobalAlarmUseCase
import com.fatih.prayertime.domain.use_case.pray_times_use_cases.insert_pray_time_into_db_use_case.InsertPrayTimeIntoDbUseCase
import com.fatih.prayertime.domain.use_case.alarm_use_cases.update_global_alarm_use_case.UpdateGlobalAlarmUseCase
import com.fatih.prayertime.util.PrayTimesString
import com.fatih.prayertime.util.Resource
import com.fatih.prayertime.util.Status
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainScreenViewModel @Inject constructor(
    private val getDailyPrayTimesFromApiUseCase: GetDailyPrayTimesFromApiUseCase,
    private val getLocationAndAddressUseCase: GetLocationAndAddressUseCase,
    private val formattedDateUseCase: FormattedDateUseCase,
    private val formattedTimeUseCase: FormattedTimeUseCase,
    private val getDailyPrayTimesAtAddressFromDatabaseUseCase: GetDailyPrayTimesAtAddressFromDatabaseUseCase,
    private val insertPrayTimeIntoDbUseCase : InsertPrayTimeIntoDbUseCase,
    private val getLastKnownAddressFromDatabaseUseCase: GetLastKnowAddressFromDatabaseUseCase,
    private val getAllGlobalAlarmsUseCase: GetAllGlobalAlarmsUseCase,
    private val getGlobalAlarmByTypeUseCase: GetGlobalAlarmByTypeUseCase,
    private val insertGlobalAlarmUseCase : InsertGlobalAlarmUseCase,
    private val updateGlobalAlarmUseCase: UpdateGlobalAlarmUseCase,
    private val getHourAndMinuteFromLongUseCase: GetHourAndMinuteFromLongUseCase
) : ViewModel() {


    //Pray - Times

    private val _dailyPrayTimes = MutableStateFlow<Resource<PrayTimes>>(Resource.loading())
    val dailyPrayTimes: StateFlow<Resource<PrayTimes>> = _dailyPrayTimes

    private var lastKnowAddress : MutableStateFlow<Address?> = MutableStateFlow(null)

    fun trackLocationAndUpdatePrayTimesDatabase() = viewModelScope.launch(Dispatchers.IO) {
        getLocationAndAddressUseCase().collect { address ->
            when(address.status){
                Status.SUCCESS -> {
                    getDailyPrayTimesFromAPI(address.data!!)
                }
                Status.LOADING -> {
                    //_currentAddress.emit(Resource.loading())
                }
                Status.ERROR -> {
                    getDailyPrayTimesFromDb()
                }
            }
        }
    }

    fun getDailyPrayTimesFromAPI(address: Address?) = viewModelScope.launch(Dispatchers.Default) {
        val searchAddress = address?:lastKnowAddress.value?:return@launch
        val getPrayTimesAtAddressFromDatabase = getDailyPrayTimesAtAddressFromDatabaseUseCase(searchAddress,formattedDate.value)
        getPrayTimesAtAddressFromDatabase?.let {
            if (it.isNotEmpty()){
                _dailyPrayTimes.emit(Resource.success(it[0]))
                return@launch
            }
        }
        val response = getDailyPrayTimesFromApiUseCase(formattedDate.value,searchAddress)
        if (response.status == Status.SUCCESS){
            _dailyPrayTimes.emit(response)
            insertPrayTimeIntoDbUseCase(response.data!!)
        }
    }

    fun getDailyPrayTimesFromDb() = viewModelScope.launch(Dispatchers.Default){
        val lastAddress = lastKnowAddress.value ?: async {
            getLastKnownAddressFromDatabaseUseCase()
        }.await()?:return@launch
        val response = getDailyPrayTimesAtAddressFromDatabaseUseCase(lastAddress, formattedDate.value )
        response?.let {
            if (it.isNotEmpty()){
                _dailyPrayTimes.emit(Resource.success(it[0]))
            }
        }
    }


    //Date

    private val _formattedDate : MutableStateFlow<String> = MutableStateFlow("")
    val formattedDate : StateFlow<String> = _formattedDate

    fun updateFormattedDate() = viewModelScope.launch(Dispatchers.Default){
        _formattedDate.emit(formattedDateUseCase())
    }

    private val _formattedTime : MutableStateFlow<String> = MutableStateFlow("")
    val formattedTime : StateFlow<String> = _formattedTime

    fun updateFormattedTime() = viewModelScope.launch(Dispatchers.Default){
        _formattedTime.emit(formattedTimeUseCase())
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
        alarmTime: Long,
        isEnabled: Boolean,
        alarmOffset: Int
    ) = viewModelScope.launch(Dispatchers.Default){

        try {
            val globalAlarm = GlobalAlarm(alarmType,alarmTime,isEnabled,alarmOffset)
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
        viewModelScope.launch(Dispatchers.Default) {
            try {
                lastKnowAddress.emit(getLastKnownAddressFromDatabaseUseCase())
                getAllGlobalAlarmsUseCase().collect { globalAlarmList ->
                    if (globalAlarmList.isEmpty()) {
                        val initialAlarms = PrayTimesString.entries.map {
                            GlobalAlarm(
                                alarmType = it.name,
                                alarmTime = 0L,
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
                println(e.message)
            }
        }
    }


}