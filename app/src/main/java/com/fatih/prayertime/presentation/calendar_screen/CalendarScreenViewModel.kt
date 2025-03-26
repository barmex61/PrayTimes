package com.fatih.prayertime.presentation.calendar_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fatih.prayertime.domain.model.IslamicDaysData
import com.fatih.prayertime.domain.use_case.formatted_use_cases.FormattedUseCase
import com.fatih.prayertime.domain.use_case.islamic_calendar_use_case.GetIslamicCalendarForMonthUseCase
import com.fatih.prayertime.util.config.ApiConfig.selectedIslamicCalendarMethod
import com.fatih.prayertime.util.extensions.withRetry
import com.fatih.prayertime.util.model.state.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDate
import javax.inject.Inject

@HiltViewModel
class CalendarScreenViewModel @Inject constructor(
    private val formattedUseCase: FormattedUseCase,
    private val getIslamicCalendarForMonthUseCase: GetIslamicCalendarForMonthUseCase,
    ): ViewModel() {

    private val _retryTrigger = MutableSharedFlow<Unit>()

    private val _searchLocalDate = MutableStateFlow(LocalDate.now())
    val searchLocalDate = _searchLocalDate

    val monthlyIslamicCalendar = _searchLocalDate.withRetry(_retryTrigger)
        .map { getIslamicCalendarForMonthUseCase(searchLocalDate.value.monthValue, searchLocalDate.value.year, selectedIslamicCalendarMethod)
    }.stateIn( scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(1000),
            initialValue = Resource.loading())


    fun updateSearchMonthAndYear(localDate: LocalDate){
        _searchLocalDate.value = localDate
    }

    fun formatLocalDateToString(localDate: LocalDate) : String {
        return formattedUseCase.formatLocalDateToMMYYYY(localDate)
    }

    fun isToday(daysData: IslamicDaysData) = formattedUseCase.isToday(daysData.date)

    fun retryCalendarLoading() = viewModelScope.launch{
        _retryTrigger.emit(Unit)
    }

    init {
        updateSearchMonthAndYear(LocalDate.now())
    }
}