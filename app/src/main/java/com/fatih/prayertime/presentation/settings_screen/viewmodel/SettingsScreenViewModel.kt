package com.fatih.prayertime.presentation.settings_screen.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fatih.prayertime.domain.model.GlobalAlarm
import com.fatih.prayertime.domain.use_case.alarm_use_cases.UpdateGlobalAlarmUseCase
import com.fatih.prayertime.util.getAlarmTimeForPrayTimes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsScreenViewModel @Inject constructor(
    private val updateGlobalAlarmUseCase: UpdateGlobalAlarmUseCase
) : ViewModel() {

    fun updateGlobalAlarm(globalAlarm: GlobalAlarm,closeDialog : () -> Unit) = viewModelScope.launch(Dispatchers.IO) {
        val alarmTime = getAlarmTimeForPrayTimes(dailyPrayTimes.value.data!!,globalAlarm.alarmType,globalAlarm.alarmOffset,formattedUseCase)
        val alarmTimeLong = formattedUseCase.formatHHMMtoLong(alarmTime)
        val alarmTimeString = formattedUseCase.formatLongToLocalDateTime(alarmTimeLong)
        updateGlobalAlarmUseCase(globalAlarm.copy(
            alarmTime = ,
        ))
        closeDialog()
    }
}