package com.fatih.prayertime.presentation.settings_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fatih.prayertime.domain.model.PrayerAlarm
import com.fatih.prayertime.domain.model.PrayTimes
import com.fatih.prayertime.domain.model.Settings
import com.fatih.prayertime.domain.model.ThemeOption
import com.fatih.prayertime.domain.use_case.alarm_use_cases.GetAllGlobalAlarmsUseCase
import com.fatih.prayertime.domain.use_case.alarm_use_cases.UpdateGlobalAlarmUseCase
import com.fatih.prayertime.domain.use_case.formatted_use_cases.FormattedUseCase
import com.fatih.prayertime.domain.use_case.location_use_cases.GetLastKnowAddressFromDatabaseUseCase
import com.fatih.prayertime.domain.use_case.pray_times_use_cases.GetDailyPrayTimesWithAddressAndDateUseCase
import com.fatih.prayertime.domain.use_case.settings_use_cases.GetSettingsUseCase
import com.fatih.prayertime.domain.use_case.settings_use_cases.SaveSettingsUseCase
import com.fatih.prayertime.util.utils.AlarmUtils.getPrayTimeForPrayType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDate
import javax.inject.Inject

@HiltViewModel
class SettingsScreenViewModel @Inject constructor(
    private val updateGlobalAlarmUseCase: UpdateGlobalAlarmUseCase,
    private val formattedUseCase: FormattedUseCase,
    private val getDailyPrayTimesWithAddressAndDateUseCase: GetDailyPrayTimesWithAddressAndDateUseCase,
    private val getLastKnowAddressFromDatabaseUseCase: GetLastKnowAddressFromDatabaseUseCase,
    private val saveSettingsUseCase: SaveSettingsUseCase,
    private val getSettingsUseCase: GetSettingsUseCase,
    private val getAllGlobalAlarmsUseCase: GetAllGlobalAlarmsUseCase
) : ViewModel() {

    private var dailyPrayTimes : PrayTimes? = null

    fun updateGlobalAlarm(prayerAlarm: PrayerAlarm, closeDialog : () -> Unit) = viewModelScope.launch(Dispatchers.IO) {
        dailyPrayTimes?:return@launch
        val prayTime = getPrayTimeForPrayType(dailyPrayTimes!!,prayerAlarm.alarmType,prayerAlarm.alarmOffset,formattedUseCase)
        val prayTimeLong = formattedUseCase.formatHHMMtoLong(prayTime,formattedUseCase.formatDDMMYYYYDateToLocalDate(dailyPrayTimes!!.date))
        val prayTimeString = formattedUseCase.formatLongToLocalDateTime(prayTimeLong)
        updateGlobalAlarmUseCase(prayerAlarm.copy(isEnabled = true, alarmTime = prayTimeLong, alarmTimeString = prayTimeString))
        closeDialog()
    }


    private val _settingsState = MutableStateFlow(Settings())
    val settingsState = _settingsState.asStateFlow()

    fun updateTheme(theme: ThemeOption) = viewModelScope.launch {
        val updatedSettings = _settingsState.value.copy(selectedTheme = theme)
        saveSettingsUseCase(updatedSettings)
    }

    fun toggleVibration() = viewModelScope.launch {
        val updatedSettings = _settingsState.value.copy(vibrationEnabled = !_settingsState.value.vibrationEnabled)
        saveSettingsUseCase(updatedSettings)
    }

    fun toggleCuma() = viewModelScope.launch {
        val updatedSettings = _settingsState.value.copy(silenceWhenCuma = !_settingsState.value.silenceWhenCuma)
        saveSettingsUseCase(updatedSettings)
    }

    fun togglePrayerNotification(prayerAlarm : PrayerAlarm) = viewModelScope.launch {
        updateGlobalAlarmUseCase(prayerAlarm)
    }

    init {

        viewModelScope.launch(Dispatchers.IO) {
            getSettingsUseCase.invoke().distinctUntilChanged()
                .collectLatest{ settings ->
                    _settingsState.value = settings
                }
        }
        viewModelScope.launch(Dispatchers.IO) {
            getAllGlobalAlarmsUseCase().collectLatest {
                val updatedSettings = _settingsState.value.copy(prayerAlarms = it)
                saveSettingsUseCase(updatedSettings)
            }
        }
    }


    init {
        viewModelScope.launch(Dispatchers.IO){
            val lastKnownLocation = getLastKnowAddressFromDatabaseUseCase() ?: return@launch
            dailyPrayTimes = getDailyPrayTimesWithAddressAndDateUseCase(lastKnownLocation,formattedUseCase.formatOfPatternDDMMYYYY(LocalDate.now()))
        }
    }
}