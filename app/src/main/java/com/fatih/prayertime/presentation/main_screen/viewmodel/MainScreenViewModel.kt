package com.fatih.prayertime.presentation.main_screen.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fatih.prayertime.data.local.entity.AlarmTimes
import com.fatih.prayertime.domain.model.Address
import com.fatih.prayertime.domain.model.PrayTimes
import com.fatih.prayertime.domain.use_case.formatted_date_use_case.FormattedDateUseCase
import com.fatih.prayertime.domain.use_case.formatted_time_use_case.FormattedTimeUseCase
import com.fatih.prayertime.domain.use_case.get_alarm_times_use_case.GetAlarmTimesUseCase
import com.fatih.prayertime.domain.use_case.get_daily_pray_times_use_case.GetDailyPrayTimesFromApiUseCase
import com.fatih.prayertime.domain.use_case.get_last_known_address_from_database_use_case.GetLastKnowAddressFromDatabaseUseCase
import com.fatih.prayertime.domain.use_case.get_location_and_adress_use_case.GetLocationAndAddressUseCase
import com.fatih.prayertime.domain.use_case.get_pray_times_at_address_from_database_use_case.GetDailyPrayTimesAtAddressFromDatabaseUseCase
import com.fatih.prayertime.domain.use_case.insert_alarm_times_use_case.InsertAlarmTimesUseCase
import com.fatih.prayertime.domain.use_case.insert_pray_time_into_db_use_case.InsertPrayTimeIntoDbUseCase
import com.fatih.prayertime.util.Resource
import com.fatih.prayertime.util.Status
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filter
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
    private val getAlarmTimesUseCase: GetAlarmTimesUseCase,
    private val insertAlarmTimesUseCase: InsertAlarmTimesUseCase
) : ViewModel() {


    //Pray - Times

    private val _dailyPrayTimes = MutableStateFlow<Resource<PrayTimes>>(Resource.loading())
    val dailyPrayTimes: StateFlow<Resource<PrayTimes>> = _dailyPrayTimes

    private var lastKnowAddress : MutableStateFlow<Address?> = MutableStateFlow(null)

    fun trackLocationAndUpdatePrayTimesDatabase() = viewModelScope.launch(Dispatchers.IO) {
        getLocationAndAddressUseCase().collect { address ->
            when(address.status){
                Status.SUCCESS -> {
                    println("Current address taking from API SUCCESS and SAVE TO DATABASE")
                    getDailyPrayTimesFromAPI(address.data!!)
                }
                Status.LOADING -> {
                    println("loading")
                    //_currentAddress.emit(Resource.loading())
                }
                Status.ERROR -> {
                    println("error")
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
        println("fromdb")
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

    private val _alarmTimes : MutableStateFlow<AlarmTimes?> = MutableStateFlow(null)
    val alarmTimes : StateFlow<AlarmTimes?> = _alarmTimes

    fun insertAlarmTimes(alarmTimes: AlarmTimes) = viewModelScope.launch(Dispatchers.Default){
        try {
            insertAlarmTimesUseCase(alarmTimes)
        }catch (e:Exception){
            println(e.message)
        }
    }

    init {
        updateFormattedDate()
        updateFormattedTime()
        viewModelScope.launch(Dispatchers.Default) {
            lastKnowAddress.emit(getLastKnownAddressFromDatabaseUseCase())
        }

        viewModelScope.launch(Dispatchers.Default){
            try {
                _alarmTimes.emit(getAlarmTimesUseCase())
                if (_alarmTimes.value == null){
                    insertAlarmTimes(
                        AlarmTimes(
                            Pair(false,null),
                            Pair(false,null),
                            Pair(false,null),
                            Pair(false,null),
                            Pair(false,null),
                        )
                    )
                }
            }catch (e:Exception){
                println(e.message)
            }
        }

    }
}