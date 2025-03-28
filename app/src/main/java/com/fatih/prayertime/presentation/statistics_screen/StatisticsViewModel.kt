package com.fatih.prayertime.presentation.statistics_screen

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fatih.prayertime.domain.use_case.formatted_use_cases.FormattedUseCase
import com.fatih.prayertime.domain.use_case.statistics_use_cases.GetStatisticsUseCase
import com.fatih.prayertime.util.model.state.StatisticsState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import org.threeten.bp.LocalDate
import java.util.Locale
import javax.inject.Inject
import kotlin.text.toFloat

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    getStatisticsUseCase: GetStatisticsUseCase,
    private val formattedUseCase: FormattedUseCase,
) : ViewModel() {


    private val _dateRange = MutableStateFlow(LocalDate.now().minusWeeks(1)..LocalDate.now())
    val dateRange = _dateRange

    fun updateDateRange(newRange: ClosedRange<LocalDate>) {
        _dateRange.value = newRange
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val statisticsState: StateFlow<StatisticsState> = dateRange.flatMapLatest {
        getStatisticsUseCase(
            formattedUseCase.formatLocalDateToLong(it.start),
            formattedUseCase.formatLocalDateToLong(it.endInclusive)
        )

    }.onEach {
        it.forEach {
            println("Type ${it.prayerType}")
            println("Date ${it.date}")
            println("IsCompleted ${it.isCompleted}")
        }
    }.mapLatest { stats ->
        if (stats.isEmpty()) {
            StatisticsState(
                startDate = "",
                endDate = "",
                totalPrayers = 0,
                completedPrayers = 0,
                missedPrayers = 0,
                statistics = emptyList()
            )
        } else {
            StatisticsState(
                startDate = stats.first().date,
                endDate = stats.last().date,
                totalPrayers = stats.size ,
                completedPrayers = stats.count { it.isCompleted },
                missedPrayers = stats.count { !it.isCompleted },
                statistics = stats
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = StatisticsState()
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    val longestSeries = statisticsState.mapLatest { statState ->
        var maxStreak = 0
        var currentStreak = 0

        statState.statistics.groupBy { it.date }
            .forEach { (_, prayers) ->
                if (prayers.all { it.isCompleted }) {
                    currentStreak++
                    if (currentStreak > maxStreak) {
                        maxStreak = currentStreak
                    }
                } else {
                    currentStreak = 0
                }
            }
        println("maxStreak $maxStreak")
        maxStreak
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    @SuppressLint("DefaultLocale")
    val completePercentageMap: StateFlow<Map<String, Float>> = statisticsState.mapLatest { statState ->
        val map = mutableMapOf<String, Float>()
        statState.statistics.groupBy {
            it.prayerType
        }.forEach {
            map[it.key] = String.format(Locale.US,"%.1f", (it.value.count { it.isCompleted }.toFloat() / it.value.size * 100)).toFloat()
        }
        map
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = mutableMapOf()
    )

}

