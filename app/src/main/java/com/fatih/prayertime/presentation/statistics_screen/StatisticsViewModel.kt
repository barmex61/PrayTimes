package com.fatih.prayertime.presentation.statistics_screen

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fatih.prayertime.R
import com.fatih.prayertime.data.local.entity.PrayerStatisticsEntity
import com.fatih.prayertime.domain.use_case.formatted_use_cases.FormattedUseCase
import com.fatih.prayertime.domain.use_case.statistics_use_cases.GetPrayerCountsUseCase
import com.fatih.prayertime.domain.use_case.statistics_use_cases.GetStatisticsUseCase
import com.fatih.prayertime.domain.use_case.statistics_use_cases.InsertPlayerStatisticsUseCase
import com.fatih.prayertime.util.model.state.StatisticsState
import dagger.hilt.android.internal.Contexts.getApplication
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDate
import javax.inject.Inject

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    getStatisticsUseCase: GetStatisticsUseCase,
    getPrayerCountsUseCase: GetPrayerCountsUseCase,
    private val formattedUseCase: FormattedUseCase,
    private val insertPlayerStatisticsUseCase: InsertPlayerStatisticsUseCase
) : ViewModel() {

    init {
        insertDummyData()
    }

    private fun insertDummyData() {
        viewModelScope.launch {
            val dummyData = mutableListOf<PrayerStatisticsEntity>()
            val prayerNames = listOf("Fajr", "Dhuhr", "Asr", "Maghrib", "Isha")

            for (day in 1..31) {
                val date = String.format("%02d-03-2025", day)
                prayerNames.forEachIndexed { index, prayerName ->
                    dummyData.add(
                        PrayerStatisticsEntity(
                            id = day * 5 + index,
                            prayerType = prayerName,
                            date = date,
                            isCompleted = listOf(true,false).random(),// Alternate between true and false, ,
                            dateLong = formattedUseCase.formatDDMMYYYYtoLong(date)
                        )
                    )
                }
            }
            dummyData.forEach { insertPlayerStatisticsUseCase(it) }
        }
    }

    private val _dateRange = MutableStateFlow(LocalDate.now().minusWeeks(1)..LocalDate.now())
    val dateRange = _dateRange

    fun updateDateRange(newRange: ClosedRange<LocalDate>) {
        _dateRange.value = newRange
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val statisticsState: StateFlow<StatisticsState> = dateRange.flatMapLatest {
        val startDate = formattedUseCase.formatOfPatternDDMMYYYY(it.start)
        val endDate = formattedUseCase.formatOfPatternDDMMYYYY(it.endInclusive)

        // Log the input date range
        println("Fetching statistics from $startDate to $endDate")
        getStatisticsUseCase(
            formattedUseCase.formatLocalDateToLong(it.start),
            formattedUseCase.formatLocalDateToLong(it.endInclusive)
        )

    }.map { stats ->
        println(stats)
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
                totalPrayers = stats.size * 5,
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

    val longestSeries = statisticsState.map { statState ->
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

        maxStreak
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0
    )


}

