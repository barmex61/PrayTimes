package com.fatih.prayertime.presentation.main_screen

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fatih.prayertime.data.di.MainScreenLocation
import com.fatih.prayertime.data.settings.PermissionAndPreferences
import com.fatih.prayertime.domain.model.PrayerAlarm
import com.fatih.prayertime.domain.model.Address
import com.fatih.prayertime.domain.model.PrayTimes
import com.fatih.prayertime.domain.model.Weather
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
import com.fatih.prayertime.domain.use_case.settings_use_cases.GetSettingsUseCase
import com.fatih.prayertime.util.model.event.MainScreenEvent
import com.fatih.prayertime.util.model.state.NetworkState
import com.fatih.prayertime.util.model.state.PrayerState
import com.fatih.prayertime.util.model.state.SelectedDuaState
import com.fatih.prayertime.util.model.state.Status
import com.fatih.prayertime.util.model.state.WeatherState
import com.fatih.prayertime.util.utils.AlarmUtils.getPrayTimeForPrayType
import com.fatih.prayertime.util.model.AladhanApiOffsets
import com.fatih.prayertime.util.utils.LocationUtils
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
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
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
    private val getSettingsUseCase: GetSettingsUseCase,
    val permissionsAndPreferences: PermissionAndPreferences,
    private val mainScreenStateManager: MainScreenStateManager,
    getDuaUseCase: GetDuaUseCase
) : ViewModel() {

    companion object{
        private const val TAG = "MainScreenViewModel"
    }

    val isNotificationPermissionGranted = permissionsAndPreferences.isNotificationPermissionGranted

    private val _isLocationTracking : MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isLocationTracking : StateFlow<Boolean> = _isLocationTracking

    private val retryTrigger = MutableSharedFlow<Unit>()

    private val _prayerUiState = MutableStateFlow<PrayerState>(PrayerState())
    val prayerUiState = _prayerUiState.asStateFlow()

    private val _weatherUiState = MutableStateFlow<WeatherState>(WeatherState())
    val weatherUiState = _weatherUiState.asStateFlow()

    private var currentMethodId: Int = 13 // Varsayılan metod ID'si
    private var currentTuneValues: Map<String, Int> = emptyMap()

    fun trackLocation() = viewModelScope.launch(Dispatchers.IO) {
        _isLocationTracking.value = true
        getLocationAndAddressUseCase()
            .catch{ exception ->
                _isLocationTracking.value = false
            }
            .collect { resource ->
                when(resource.status){
                    Status.SUCCESS ->{
                        mainScreenStateManager.updateAddress(resource.data!!)
                    }
                    Status.ERROR ->{
                        _prayerUiState.update { it.copy(error = resource.message) }
                    }
                    else ->{
                        _prayerUiState.update { it.copy(isLoading = true) }
                    }
                }
            }
    }

    private suspend fun fetchPrayTimes(address: Address){
        val prayTimesDb = fetchPrayTimesByDatabase(address)
        if (prayTimesDb != null){
            mainScreenStateManager.updatePrayTimes(prayTimesDb)
            _prayerUiState.value = _prayerUiState.value.copy(prayTimes = prayTimesDb, isLoading = false, error = null)
            return
        }
        val prayTimesApi = fetchPrayTimesByApi(address)
        if (prayTimesApi != null){
            mainScreenStateManager.updatePrayTimes(prayTimesApi)
            _prayerUiState.value = _prayerUiState.value.copy(prayTimes = prayTimesApi, isLoading = false, error = null)
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
        
        val settings = getSettingsUseCase.invoke().first()
        
        val defaultOffsets = AladhanApiOffsets.getDefaultOffsets(settings.prayerCalculationMethod)
        val customOffsets = settings.prayerTimeTuneValues
        val combinedOffsets = defaultOffsets.toMutableMap()
        
        customOffsets.forEach { (key, value) ->
            if (combinedOffsets.containsKey(key)) {
                combinedOffsets[key] = combinedOffsets[key]!! + value
            }
        }
        
        val tuneString = AladhanApiOffsets.formatTuneString(combinedOffsets)
        
        Log.d(TAG, "Hesaplama Metodu: ${settings.prayerCalculationMethod} - ${getCalculationMethodName(settings.prayerCalculationMethod)}")
        Log.d(TAG, "Varsayılan offset değerleri: $defaultOffsets")
        Log.d(TAG, "Kullanıcı offset değerleri: $customOffsets")
        Log.d(TAG, "Birleştirilmiş offset değerleri: $combinedOffsets")
        Log.d(TAG, "API'ye gönderilen tune değeri: $tuneString")
        
        val apiResponse = getMonthlyPrayTimesFromApiUseCase(
            year = year,
            month = month, 
            address = address,
            method = settings.prayerCalculationMethod,
            tuneString = tuneString,
            school = 0
        )
        
        return if (apiResponse.status == Status.SUCCESS){
            insertPrayTimeIntoDbUseCase.insertPrayTimeList(apiResponse.data!!)
            apiResponse.data.firstOrNull{it.date == _formattedDate.value}
        }
        else null
    }

    private fun fetchWeatherByCoordinates(latitude: Double, longitude: Double) {
        viewModelScope.launch {
            _weatherUiState.value = _weatherUiState.value.copy(isWeatherLoading = true)
            getWeatherUseCase.getByCoordinates(latitude, longitude).collect { result ->
                when(result.status) {
                    Status.SUCCESS -> {

                        mainScreenStateManager.updateWeather(result.data)
                        _weatherUiState.value = _weatherUiState.value.copy(
                            weather = result.data,
                            isWeatherLoading = false,
                            weatherError = null
                        )
                        
                        Log.d(TAG, "Hava durumu güncellendi: ${result.data?.locationName}")
                    }
                    Status.ERROR -> {
                        _weatherUiState.value = _weatherUiState.value.copy(
                            isWeatherLoading = false,
                            weatherError = result.message
                        )
                        Log.e(TAG, "Hava durumu güncellenirken hata: ${result.message}")
                    }
                    Status.LOADING -> {
                        _weatherUiState.value = _weatherUiState.value.copy(
                            isWeatherLoading = true
                        )
                    }
                }
            }
        }
    }

    private fun updateWeatherIfNeeded(address: Address) {
        val currentWeather = mainScreenStateManager.weatherState.value
        
        if (currentWeather == null) {
            Log.d(TAG, "İlk hava durumu verisi yükleniyor")
            fetchWeatherByCoordinates(address.latitude, address.longitude)
            return
        }
        
        val isSignificantChange = LocationUtils.isSignificantLocationChange(
            currentWeather.latitude, currentWeather.longitude,
            address.latitude, address.longitude,
            5.0
        )
        
        if (isSignificantChange) {
            Log.d(TAG, "Önemli konum değişikliği tespit edildi: ${calculateDistance(currentWeather, address)} km. Hava durumu güncelleniyor.")
            fetchWeatherByCoordinates(address.latitude, address.longitude)
        } else {
            Log.d(TAG, "Konum değişikliği çok az: ${calculateDistance(currentWeather, address)} km. Hava durumu güncellenmeyecek.")
        }
    }

    private fun calculateDistance(weather: Weather, address: Address): Double {
        return LocationUtils.calculateDistance(
            weather.latitude, weather.longitude,
            address.latitude, address.longitude
        )
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
            val dailyPrayTimes = prayerUiState.value.prayTimes
            dailyPrayTimes?:return@launch
            val prayTime = getPrayTimeForPrayType(prayerUiState.value.prayTimes!!,globalAlarm.alarmType,globalAlarm.alarmOffset,formattedUseCase)
            val prayTimeLong = formattedUseCase.formatHHMMtoLong(prayTime,formattedUseCase.formatDDMMYYYYDateToLocalDate(dailyPrayTimes.date))
            val prayTimeString = formattedUseCase.formatLongToLocalDateTime(prayTimeLong)
            updateGlobalAlarmUseCase(globalAlarm.copy(isEnabled = if (enableAllGlobalAlarm) true else globalAlarm.isEnabled, alarmTime = prayTimeLong, alarmTimeString = prayTimeString))
        }
    }


    fun getAlarmTime(index: Int) : Pair<Long,String>  {
        val currentPrayTimes = prayerUiState.value.prayTimes
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


    private fun getCalculationMethodName(methodId: Int): String {
        return when (methodId) {
            0 -> "Shia Ithna-Ashari"
            1 -> "University of Islamic Sciences, Karachi"
            2 -> "Islamic Society of North America"
            3 -> "Muslim World League"
            4 -> "Umm Al-Qura University, Makkah"
            5 -> "Egyptian General Authority of Survey"
            7 -> "Institute of Geophysics, University of Tehran"
            8 -> "Gulf Region"
            9 -> "Kuwait"
            10 -> "Qatar"
            11 -> "Majlis Ugama Islam Singapura"
            12 -> "Union Organization Islamic de France"
            13 -> "Diyanet İşleri Başkanlığı"
            14 -> "Algeria"
            15 -> "Türkiye Takvimi"
            16 -> "Russia"
            17 -> "Moonsighting Committee Worldwide (Moonsighting.com)"
            18 -> "Dubai"
            19 -> "United Arab Emirates"
            20 -> "Jakim, Malaysia"
            21 -> "Tunisia"
            22 -> "Afghanistan"
            23 -> "Bosnia and Herzegovina"
            else -> "Bilinmeyen Metod"
        }
    }

    private fun refreshPrayTimesForSettings(address: Address) = viewModelScope.launch {
        _prayerUiState.update { it.copy(isLoading = true) }
        
        val prayTimesApi = fetchPrayTimesByApi(address)
        
        if (prayTimesApi != null) {
            mainScreenStateManager.updatePrayTimes(prayTimesApi)
            _prayerUiState.update { it.copy(prayTimes = prayTimesApi, isLoading = false, error = null) }
            updateAllGlobalAlarm(false)
        } else {
            _prayerUiState.update { it.copy(isLoading = false, error = "Namaz vakitleri güncellenemedi") }
        }
    }

    init {
        updateFormattedDate()
        updateFormattedTime()
        updateAllGlobalAlarm(false)
        checkNotificationPermission()

        viewModelScope.launch(Dispatchers.IO) {

            if (mainScreenStateManager.addressState.value == null) {
                mainScreenStateManager.updateAddress(getLastKnownAddressFromDatabaseUseCase())
            }

            launch {
                val initialSettings = getSettingsUseCase.invoke().first()
                currentMethodId = initialSettings.prayerCalculationMethod
                currentTuneValues = initialSettings.prayerTimeTuneValues
            }

            launch {
                mainScreenStateManager.prayTimesState
                    .filterNotNull()
                    .collect { prayTimes ->
                        _prayerUiState.update { it.copy(prayTimes = prayTimes, isLoading = false, error = null) }
                    }
            }
            
            launch {
                mainScreenStateManager.weatherState
                    .filterNotNull()
                    .collect { weather ->
                        _weatherUiState.update { it.copy(weather = weather, isWeatherLoading = false, weatherError = null) }
                    }
            }
            
            launch {
                permissionsAndPreferences.networkState.combine(permissionsAndPreferences.isLocationPermissionGranted){networkState,locationPermission->
                    networkState to locationPermission
                }.distinctUntilChanged().collect { (networkState,locationPermission)->
                    if (!isLocationTracking.value && locationPermission && networkState == NetworkState.Connected){
                        trackLocation()
                    }
                    if (locationPermission && prayerUiState.value.prayTimes == null){
                        fetchPrayTimesByDatabase(null)
                    }
                }
            }
            
            launch {
                getAllGlobalAlarmsUseCase().collect { globalAlarmList ->
                    _prayerAlarmList.emit(globalAlarmList)
                }
            }

            
            launch {
                mainScreenStateManager.addressState
                    .filterNotNull()
                    .distinctUntilChanged()
                    .collectLatest { address ->
                        fetchPrayTimes(address)
                        println("district ${address.district}")
                        println(address)
                        updateWeatherIfNeeded(address)
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
                _prayerUiState
                    .map { it.prayTimes }
                    .filterNotNull()
                    .take(1)
                    .collect { prayTimes ->
                        val isStatisticsAlarmInitialized = getStatisticSharedPrefUseCase()
                        if (!isStatisticsAlarmInitialized) {
                            try {
                                updateStatisticsAlarmUseCase.updateStatisticsAlarms(prayTimes)
                                insertStatisticSharedPrefUseCase()
                            } catch (e: Exception) {
                                println("Statistics alarm kurulumunda hata: ${e.message}")
                            }
                        } else {
                            println("Statistics alarm zaten kurulmuş")
                        }
                    }
            }
            launch {
                getSettingsUseCase.invoke()
                    .distinctUntilChanged { old, new ->
                        val methodUnchanged = old.prayerCalculationMethod == new.prayerCalculationMethod
                        val tuneValuesUnchanged = old.prayerTimeTuneValues == new.prayerTimeTuneValues
                        methodUnchanged && tuneValuesUnchanged
                    }
                    .collect { settings ->
                        val methodChanged = currentMethodId != settings.prayerCalculationMethod
                        val tuneValuesChanged = currentTuneValues != settings.prayerTimeTuneValues

                        if (methodChanged || tuneValuesChanged) {
                            currentMethodId = settings.prayerCalculationMethod
                            currentTuneValues = settings.prayerTimeTuneValues

                            Log.d(TAG, "Ayarlar değişti. Yeni hesaplama metodu: $currentMethodId, yeni offset değerleri: $currentTuneValues")

                            mainScreenStateManager.addressState.value?.let { address ->
                                refreshPrayTimesForSettings(address)
                            }
                        }
                    }
            }
        }

    }
}