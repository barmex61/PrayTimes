package com.fatih.prayertime.presentation.statistics_screen

import android.annotation.SuppressLint
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
import kotlin.text.toFloat

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

        // Log the input date range
        getStatisticsUseCase(
            formattedUseCase.formatLocalDateToLong(it.start),
            formattedUseCase.formatLocalDateToLong(it.endInclusive)
        )

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

        maxStreak
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    @SuppressLint("DefaultLocale")
    val completePercentageList: StateFlow<List<Float>> = statisticsState.mapLatest { statState ->
        val list = mutableListOf<Float>()
        val totalPrayers = statState.totalPrayers.toFloat()
        statState.statistics.groupBy {
            it.prayerType
        }.forEach {
            list.add(String.format("%.1f", (it.value.count { it.isCompleted }.toFloat() / totalPrayers * 100)).toFloat())
        }
        list
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = mutableListOf()
    )

}

