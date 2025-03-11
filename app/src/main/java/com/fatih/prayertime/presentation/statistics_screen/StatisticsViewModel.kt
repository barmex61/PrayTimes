package com.fatih.prayertime.presentation.statistics_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fatih.prayertime.data.local.entity.PrayerStatisticsEntity
import com.fatih.prayertime.domain.use_case.formatted_use_cases.FormattedUseCase
import com.fatih.prayertime.domain.use_case.statistics_use_cases.GetPrayerCountsUseCase
import com.fatih.prayertime.domain.use_case.statistics_use_cases.GetStatisticsUseCase
import com.fatih.prayertime.domain.use_case.statistics_use_cases.InsertPlayerStatisticsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
import javax.inject.Inject
import kotlin.times

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    getStatisticsUseCase: GetStatisticsUseCase,
    getPrayerCountsUseCase: GetPrayerCountsUseCase,
    formattedUseCase: FormattedUseCase,
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
                            isCompleted = listOf(true,false).random() // Alternate between true and false
                        )
                    )
                }
            }
            dummyData.forEach { insertPlayerStatisticsUseCase(it) }
        }
    }

    val statistics = getStatisticsUseCase(
        formattedUseCase.formatOfPatternDDMMYYYY(LocalDate.now().minusWeeks(1).plusDays(1)),
        formattedUseCase.formatOfPatternDDMMYYYY(LocalDate.now().plusWeeks(2))
    ).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val statisticsSummary = statistics.map { stats ->
        if (stats.isEmpty()) {
            StatisticsSummary(
                startDate = "",
                endDate = "",
                totalPrayers = 0,
                completedPrayers = 0,
                missedPrayers = 0
            )
        } else {
            StatisticsSummary(
                startDate = stats.first().date,
                endDate = stats.last().date,
                totalPrayers = stats.size * 5,
                completedPrayers = stats.count { it.isCompleted },
                missedPrayers = stats.count { !it.isCompleted }
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = StatisticsSummary()
    )
}

data class StatisticsSummary(
    val startDate: String = "",
    val endDate: String = "",
    val totalPrayers: Int = 0,
    val completedPrayers: Int = 0,
    val missedPrayers: Int = 0
) 