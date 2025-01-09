package com.fatih.prayertime.presentation.main_screen.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fatih.prayertime.domain.model.Address
import com.fatih.prayertime.domain.model.DailyPrayResponse
import com.fatih.prayertime.domain.model.PrayTimes
import com.fatih.prayertime.domain.use_case.formatted_date_use_case.FormattedDateUseCase
import com.fatih.prayertime.domain.use_case.formatted_time_use_case.FormattedTimeUseCase
import com.fatih.prayertime.domain.use_case.get_address_from_database.GetAddressFromDatabaseUseCase
import com.fatih.prayertime.domain.use_case.get_daily_pray_times_use_case.GetDailyPrayTimesUseCase
import com.fatih.prayertime.domain.use_case.get_location_and_adress_use_case.GetLocationAndAddressUseCase
import com.fatih.prayertime.domain.use_case.save_address_to_database_use_case.SaveAddressToDatabaseUseCase
import com.fatih.prayertime.util.Resource
import com.fatih.prayertime.util.Status
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class MainScreenViewModel @Inject constructor(
    private val getDailyPrayTimesUseCase: GetDailyPrayTimesUseCase,
    private val getLocationAndAddressUseCase: GetLocationAndAddressUseCase,
    private val getAddressFromDatabaseUseCase: GetAddressFromDatabaseUseCase,
    private val saveAddressToDatabaseUseCase: SaveAddressToDatabaseUseCase,
    private val formattedDateUseCase: FormattedDateUseCase,
    private val formattedTimeUseCase: FormattedTimeUseCase
) : ViewModel() {


    //Pray - Times

    private val _dailyPrayTimes = MutableStateFlow<Resource<PrayTimes>>(Resource.loading())
    val dailyPrayTimes: StateFlow<Resource<PrayTimes>> = _dailyPrayTimes

    fun getDailyPrayTimes(date: String, latitude: Double, longitude: Double) = viewModelScope.launch {
        _dailyPrayTimes.emit(getDailyPrayTimesUseCase(date, latitude, longitude))
    }

    //Location - Room


    private val _currentAddress = MutableStateFlow<Resource<Address>>(Resource.loading())
    val currentAddress: StateFlow<Resource<Address>> = _currentAddress

    fun getCurrentAddressFromLive() = viewModelScope.launch {
        getLocationAndAddressUseCase().collect { address ->
            when(address.status){
                Status.SUCCESS -> {
                    println("success")
                    _currentAddress.emit(Resource.success(address.data))
                    saveAddressToDatabase(currentAddress.value.data!!)
                }
                Status.LOADING -> {
                    //_currentAddress.emit(Resource.loading())
                }
                Status.ERROR -> {
                    getAddressFromDatabaseUseCase()
                }
            }
        }
    }

    fun getCurrentAddressFromDatabase() = viewModelScope.launch(Dispatchers.Default) {
        println("fromdb")
        _currentAddress.emit(Resource.loading())
        try {
            val address = getAddressFromDatabaseUseCase()
            if (address != null){
                _currentAddress.emit(Resource.success(address))
            }else{
                _currentAddress.emit(Resource.error("Address not found"))
            }
        }catch (e:Exception){
            _currentAddress.emit(Resource.error(e.message.toString()))
        }
    }

    private fun saveAddressToDatabase(address: Address) = viewModelScope.launch(Dispatchers.Default) {
        saveAddressToDatabaseUseCase(address)
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

    init {
        updateFormattedDate()
        updateFormattedTime()

    }
}