package com.fatih.prayertime.presentation.util_screen.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fatih.prayertime.data.remote.dto.islamicdaysdto.IslamicDaysDataDTO
import com.fatih.prayertime.domain.model.IslamicDaysData
import com.fatih.prayertime.domain.use_case.formatted_use_cases.FormattedUseCase
import com.fatih.prayertime.domain.use_case.islamic_calendar_use_case.GetIslamicCalendarForMonthUseCase
import com.fatih.prayertime.util.Constants.selectedIslamicCalendarMethod
import com.fatih.prayertime.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDate
import javax.inject.Inject

@HiltViewModel
class UtilScreenViewModel @Inject constructor(
    private val getIslamicCalendarForMonthUseCase: GetIslamicCalendarForMonthUseCase,
    private val formattedUseCase: FormattedUseCase
) : ViewModel(){

    private val _monthlyIslamicCalendar = MutableStateFlow<Resource<List<IslamicDaysData>>>(Resource.loading())
    val monthlyIslamicCalendar = _monthlyIslamicCalendar

    private val _searchLocalDate = MutableStateFlow(LocalDate.now())
    val searchLocalDate = _searchLocalDate

    private fun getMonthlyIslamicCalendar(year: Int, month: Int, calendarMethod: String = selectedIslamicCalendarMethod) = viewModelScope.launch(Dispatchers.IO){
        _monthlyIslamicCalendar.value = Resource.loading()
        delay(500)
        _monthlyIslamicCalendar.value = getIslamicCalendarForMonthUseCase(year, month, calendarMethod)
    }

    fun updateSearchMonthAndYear(localDate: LocalDate){
        _searchLocalDate.value = localDate
    }

    fun formatLocalDateToString(localDate: LocalDate) : String {
        return formattedUseCase.formatLocalDateToMMYYYY(localDate)
    }

    init {
        viewModelScope.launch(Dispatchers.IO){
            _searchLocalDate.distinctUntilChanged { old, new ->
                old.monthValue == new.monthValue && old.year == new.year
            }.collect {
                getMonthlyIslamicCalendar(it.monthValue, it.year)
            }
        }
    }

}