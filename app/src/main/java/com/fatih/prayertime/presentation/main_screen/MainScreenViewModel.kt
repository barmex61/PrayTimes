package com.fatih.prayertime.presentation.main_screen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fatih.prayertime.data.di.MainScreenLocation
import com.fatih.prayertime.data.settings.PermissionAndPreferences
import com.fatih.prayertime.domain.model.PrayerAlarm
import com.fatih.prayertime.domain.model.Address
import com.fatih.prayertime.domain.model.PrayTimes
import com.fatih.prayertime.domain.use_case.formatted_use_cases.FormattedUseCase
import com.fatih.prayertime.domain.use_case.alarm_use_cases.GetAllGlobalAlarmsUseCase
import com.fatih.prayertime.domain.use_case.pray_times_use_cases.GetMonthlyPrayTimesFromApiUseCase
import com.fatih.prayertime.domain.use_case.location_use_cases.GetLastKnowAddressFromDatabaseUseCase
import com.fatih.prayertime.domain.use_case.location_use_cases.GetLocationAndAddressUseCase
import com.fatih.prayertime.domain.use_case.pray_times_use_cases.GetDailyPrayTimesWithAddressAndDateUseCase
import com.fatih.prayertime.domain.use_case.pray_times_use_cases.InsertPrayTimeIntoDbUseCase
import com.fatih.prayertime.domain.use_case.alarm_use_cases.UpdateGlobalAlarmUseCase
import com.fatih.prayertime.domain.use_case.alarm_use_cases.UpdateStatisticsAlarmUseCase
import com.fatih.prayertime.domain.use_case.dua_use_case.GetDuaUseCase
import com.fatih.prayertime.domain.use_case.location_use_cases.RemoveLocationCallbackUseCase
import com.fatih.prayertime.domain.use_case.settings_use_cases.GetStatisticSharedPrefUseCase
import com.fatih.prayertime.domain.use_case.settings_use_cases.InsertStatisticSharedPrefUseCase
import com.fatih.prayertime.domain.use_case.weather_use_cases.GetWeatherUseCase
import com.fatih.prayertime.util.model.event.MainScreenEvent
import com.fatih.prayertime.util.model.state.NetworkState
import com.fatih.prayertime.util.model.state.PrayerState
import com.fatih.prayertime.util.model.state.SelectedDuaState
import com.fatih.prayertime.util.model.state.Status
import com.fatih.prayertime.util.model.state.WeatherState
import com.fatih.prayertime.util.utils.AlarmUtils.getPrayTimeForPrayType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class MainScreenViewModel @Inject constructor(
    private val getMonthlyPrayTimesFromApiUseCase: GetMonthlyPrayTimesFromApiUseCase,
    @MainScreenLocation private val getLocationAndAddressUseCase: GetLocationAndAddressUseCase,
    private val formattedUseCase: FormattedUseCase,
    private val getDailyPrayTimesWithAddressAndDateUseCase: GetDailyPrayTimesWithAddressAndDateUseCase,
    private val insertPrayTimeIntoDbUseCase : InsertPrayTimeIntoDbUseCase,
    private val getLastKnownAddressFromDatabaseUseCase: GetLastKnowAddressFromDatabaseUseCase,
    private val getAllGlobalAlarmsUseCase: GetAllGlobalAlarmsUseCase,
    private val removeLocationCallbackUseCase: RemoveLocationCallbackUseCase,
    private val updateGlobalAlarmUseCase: UpdateGlobalAlarmUseCase,
    private val updateStatisticsAlarmUseCase: UpdateStatisticsAlarmUseCase,
    private val getStatisticSharedPrefUseCase: GetStatisticSharedPrefUseCase,
    private val insertStatisticSharedPrefUseCase: InsertStatisticSharedPrefUseCase,
    private val getWeatherUseCase: GetWeatherUseCase,
    val permissionsAndPreferences: PermissionAndPreferences,

    getDuaUseCase: GetDuaUseCase
) : ViewModel() {

    companion object{
        const val TAG = "MainScreenViewModel"
    }

    val isNotificationPermissionGranted = permissionsAndPreferences.isNotificationPermissionGranted

    private val _isLocationTracking : MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isLocationTracking : StateFlow<Boolean> = _isLocationTracking

    private val retryTrigger = MutableSharedFlow<Unit>()

    //Pray - Times

    private val searchAddressState : MutableStateFlow<Address?> = MutableStateFlow(null)

    // State

    private val _weatherState = MutableStateFlow<WeatherState>(WeatherState())
    val weatherState = _weatherState.asStateFlow()

    private val _prayerState = MutableStateFlow<PrayerState>(PrayerState())
    val prayerState = _prayerState.asStateFlow()

    fun trackLocation() = viewModelScope.launch(Dispatchers.IO) {
        _isLocationTracking.value = true
        getLocationAndAddressUseCase()
            .catch{ exception ->
                _isLocationTracking.value = false
            }
            .collect { resource ->
                when(resource.status){
                    Status.SUCCESS ->{
                        updateSearchAddress(resource.data!!)
                    }
                    Status.ERROR ->{
                        _prayerState.update { it.copy(error = resource.message) }
                    }
                    else ->{
                        _prayerState.update { it.copy(isLoading = true) }
                    }
                }
            }
    }

    private fun updateSearchAddress(address: Address) = viewModelScope.launch{
        searchAddressState.emit(address)
    }

    private suspend fun fetchPrayTimes(address: Address){
        val prayTimesDb = fetchPrayTimesByDatabase(address)
        if (prayTimesDb != null){
            _prayerState.value = _prayerState.value.copy(prayTimes = prayTimesDb, isLoading = false,error = null)
            return
        }
        val prayTimesApi = fetchPrayTimesByApi(address)
        if (prayTimesApi != null){
            _prayerState.value = _prayerState.value.copy(prayTimes = prayTimesApi,isLoading = false,error = null)
        }
        updateAllGlobalAlarm(false)
    }

    private suspend fun fetchPrayTimesByDatabase(address: Address?=null) : PrayTimes? {
        val searchAddress = address?:getLastKnownAddressFromDatabaseUseCase()?:return null
        return getDailyPrayTimesWithAddressAndDateUseCase(searchAddress,formattedDate.value)
    }

    private suspend fun fetchPrayTimesByApi(address: Address) : PrayTimes? {
        val year = LocalDateTime.now().year
        val month = LocalDateTime.now().monthValue
        val apiResponse = getMonthlyPrayTimesFromApiUseCase(year ,month,address)
        return if (apiResponse.status == Status.SUCCESS){
            insertPrayTimeIntoDbUseCase.insertPrayTimeList(apiResponse.data!!)
            apiResponse.data.firstOrNull{it.date == _formattedDate.value}
        }
        else null
    }

    private fun fetchWeatherByCoordinates(latitude: Double, longitude: Double) {
        viewModelScope.launch {
            println("fetchWeather")
            _weatherState.value = _weatherState.value.copy(isWeatherLoading = true)
            getWeatherUseCase.getByCoordinates(latitude, longitude).collect { result ->
                when(result.status) {
                    Status.SUCCESS -> {
                        _weatherState.value = _weatherState.value.copy(
                            weather = result.data,
                            isWeatherLoading = false,
                            weatherError = null
                        )
                    }
                    Status.ERROR -> {
                        _weatherState.value = _weatherState.value.copy(
                            isWeatherLoading = false,
                            weatherError = result.message
                        )
                    }
                    Status.LOADING -> {
                        _weatherState.value = _weatherState.value.copy(
                            isWeatherLoading = true
                        )
                    }
                }
            }
        }
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

    private val _prayerAlarmList : MutableStateFlow<List<PrayerAlarm>?> = MutableStateFlow(null)
    val prayerAlarmList : StateFlow<List<PrayerAlarm>?> = _prayerAlarmList

    fun updateGlobalAlarm(
        alarmType : String,
        alarmTimeLong: Long,
        alarmTimeString : String,
        isEnabled: Boolean,
        alarmOffset: Long
    ) = viewModelScope.launch(Dispatchers.Default){

        try {
            val prayerAlarm = PrayerAlarm(alarmType,alarmTimeLong,alarmTimeString,isEnabled,alarmOffset)
            updateGlobalAlarmUseCase(prayerAlarm)
        }catch (e:Exception){
            Log.d(TAG,e.message?:"Error occurred while updating global alarm")
        }
    }

    fun updateAllGlobalAlarm(enableAllGlobalAlarm : Boolean) = viewModelScope.launch(Dispatchers.IO){
        prayerAlarmList.value?.forEach { globalAlarm ->
            val dailyPrayTimes = prayerState.value.prayTimes
            dailyPrayTimes?:return@launch
            val prayTime = getPrayTimeForPrayType(prayerState.value.prayTimes!!,globalAlarm.alarmType,globalAlarm.alarmOffset,formattedUseCase)
            val prayTimeLong = formattedUseCase.formatHHMMtoLong(prayTime,formattedUseCase.formatDDMMYYYYDateToLocalDate(dailyPrayTimes.date))
            val prayTimeString = formattedUseCase.formatLongToLocalDateTime(prayTimeLong)
            updateGlobalAlarmUseCase(globalAlarm.copy(isEnabled = if (enableAllGlobalAlarm) true else globalAlarm.isEnabled, alarmTime = prayTimeLong, alarmTimeString = prayTimeString))
        }
    }


    fun getAlarmTime(index: Int) : Pair<Long,String>  {
        val currentPrayTimes = prayerState.value.prayTimes
        currentPrayTimes?:return Pair(0L,"00:00:00")
        val prayTimes = currentPrayTimes
        val timeString = when(index){
            0 -> prayTimes.morning
            1 -> prayTimes.noon
            2 -> prayTimes.afternoon
            3 -> prayTimes.evening
            else -> prayTimes.night
        }
        try {
            val timeLong = formattedUseCase.formatHHMMtoLong(timeString,formattedUseCase.formatDDMMYYYYDateToLocalDate(prayTimes.date))
            val timeStr = formattedUseCase.formatLongToLocalDateTime(timeLong)
            return Pair(timeLong, timeStr)
        }catch (e:Exception){
            Log.d(TAG,e.message?:"Error occurred while getting hour and minute from index")
            return Pair(0L,"00:00:00")
        }
    }


    fun checkNotificationPermission(){
        permissionsAndPreferences.checkNotificationPermission()
    }

    private val _duaCategoryList = getDuaUseCase.invoke()?.data
    val duaCategoryList = _duaCategoryList

    private val _mainScreenEvent = MutableSharedFlow<MainScreenEvent>()

    private val _selectedDuaState = MutableStateFlow(SelectedDuaState())
    val selectedDuaState = _selectedDuaState.asStateFlow()

    fun onEvent(event : MainScreenEvent)=viewModelScope.launch{
        _mainScreenEvent.emit(event)
    }

    private fun getRandomDua() = viewModelScope.launch {
        try {
            if (!duaCategoryList.isNullOrEmpty()) {
                val selectedDua = duaCategoryList.random().detail.random()
                _selectedDuaState.value = SelectedDuaState(
                    dua = selectedDua,
                    isVisible = true
                )
            }
        } catch (e: Exception) {
            println("Dua yüklenirken hata: ${e.message}")
        }
    }

    private fun hideDuaDialog() {
        _selectedDuaState.value = _selectedDuaState.value.copy(isVisible = false)
    }

    private fun removeCallbacks(){
        _isLocationTracking.value = false
        removeLocationCallbackUseCase()
    }

    override fun onCleared() {
        removeCallbacks()
        super.onCleared()
    }


    init {

        updateFormattedDate()
        updateFormattedTime()
        updateAllGlobalAlarm(false)
        checkNotificationPermission()

        viewModelScope.launch(Dispatchers.IO) {
            launch {
                permissionsAndPreferences.networkState.combine(permissionsAndPreferences.isLocationPermissionGranted){networkState,locationPermission->
                    networkState to locationPermission
                }.distinctUntilChanged().collect { (networkState,locationPermission)->
                    if (!isLocationTracking.value && locationPermission && networkState == NetworkState.Connected){
                        trackLocation()
                    }
                    if (locationPermission && prayerState.value.prayTimes == null){
                        fetchPrayTimesByDatabase(null)
                    }
                    if (networkState == NetworkState.Connected && weatherState.value.weather == null && searchAddressState.value != null){
                        fetchWeatherByCoordinates(searchAddressState.value!!.latitude, searchAddressState.value!!.longitude)
                    }

                }
            }
            launch {
                getAllGlobalAlarmsUseCase().collect { globalAlarmList ->
                    _prayerAlarmList.emit(globalAlarmList)
                }
            }

            launch {
                searchAddressState.emit(getLastKnownAddressFromDatabaseUseCase())
            }
            launch {
                searchAddressState.collectLatest { address->
                    val searchAddress = address?:getLastKnownAddressFromDatabaseUseCase()?:return@collectLatest
                    fetchPrayTimes(searchAddress)
                    fetchWeatherByCoordinates(searchAddress.latitude, searchAddress.longitude)
                }
            }
            launch {
                _mainScreenEvent.collect { event ->
                    when (event) {
                        is MainScreenEvent.ShowDuaDialog -> getRandomDua()
                        is MainScreenEvent.HideDuaDialog -> hideDuaDialog()
                    }
                }
            }
            launch {
                _prayerState
                    .map { it.prayTimes }
                    .filterNotNull()
                    .take(1)
                    .collect { prayTimes ->
                        val isStatisticsAlarmInitialized = getStatisticSharedPrefUseCase()
                        if (!isStatisticsAlarmInitialized) {
                            try {
                                updateStatisticsAlarmUseCase.updateStatisticsAlarms(prayTimes)
                                insertStatisticSharedPrefUseCase()
                                println("yesinit")
                            } catch (e: Exception) {
                                println("Statistics alarm kurulumunda hata: ${e.message}")
                            }
                        } else {
                            println("Statistics alarm zaten kurulmuş")
                        }
                    }
            }
        }
    }


}