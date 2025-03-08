package com.fatih.prayertime.presentation.statistics_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fatih.prayertime.domain.use_case.statistics_use_cases.GetPrayerCountsUseCase
import com.fatih.prayertime.domain.use_case.statistics_use_cases.GetStatisticsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    private val getStatisticsUseCase: GetStatisticsUseCase,
    private val getPrayerCountsUseCase: GetPrayerCountsUseCase
) : ViewModel() {

    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    private val today = LocalDate.now()
    private val lastWeek = today.minusWeeks(1)

    val statistics = getStatisticsUseCase(
        lastWeek.format(dateFormatter),
        today.format(dateFormatter)
    ).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val completedPrayersCount = getPrayerCountsUseCase.getCompletedCount()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )

    val onTimePrayersCount = getPrayerCountsUseCase.getOnTimeCount()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )
} 