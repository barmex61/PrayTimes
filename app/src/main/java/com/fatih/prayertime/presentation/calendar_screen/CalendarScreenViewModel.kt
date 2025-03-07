package com.fatih.prayertime.presentation.calendar_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fatih.prayertime.domain.model.IslamicDaysData
import com.fatih.prayertime.domain.use_case.formatted_use_cases.FormattedUseCase
import com.fatih.prayertime.domain.use_case.islamic_calendar_use_case.GetIslamicCalendarForMonthUseCase
import com.fatih.prayertime.util.Constants.selectedIslamicCalendarMethod
import com.fatih.prayertime.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDate
import javax.inject.Inject

@HiltViewModel
class CalendarScreenViewModel @Inject constructor(
    private val formattedUseCase: FormattedUseCase,
    private val getIslamicCalendarForMonthUseCase: GetIslamicCalendarForMonthUseCase,
    ): ViewModel() {

    private val _monthlyIslamicCalendar = MutableStateFlow<Resource<List<IslamicDaysData>>>(Resource.loading())
    val monthlyIslamicCalendar = _monthlyIslamicCalendar

    private val _searchLocalDate = MutableStateFlow(LocalDate.now())
    val searchLocalDate = _searchLocalDate

    fun getMonthlyIslamicCalendar( calendarMethod: String = selectedIslamicCalendarMethod) = viewModelScope.launch(Dispatchers.IO){
        _monthlyIslamicCalendar.value = Resource.loading()
        delay(500)
        _monthlyIslamicCalendar.value = getIslamicCalendarForMonthUseCase(searchLocalDate.value.monthValue, searchLocalDate.value.year, calendarMethod)
    }

    fun updateSearchMonthAndYear(localDate: LocalDate){
        _searchLocalDate.value = localDate
    }

    fun formatLocalDateToString(localDate: LocalDate) : String {
        return formattedUseCase.formatLocalDateToMMYYYY(localDate)
    }

    fun isToday(daysData: IslamicDaysData) = formattedUseCase.isToday(daysData.date)

    init {
        updateSearchMonthAndYear(LocalDate.now())
        viewModelScope.launch(Dispatchers.IO){
            _searchLocalDate.collectLatest {
                getMonthlyIslamicCalendar()
            }
        }
    }
}