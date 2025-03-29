package com.fatih.prayertime.util.model.state

import com.fatih.prayertime.data.local.entity.PrayerStatisticsEntity

data class StatisticsState(
    val startDate: String = "",
    val endDate: String = "",
    val totalPrayers: Int = 0,
    val completedPrayers: Int = 0,
    val missedPrayers: Int = 0,
    val completePercentageMap: Map<String, Float> = emptyMap(),
    val statistics: List<PrayerStatisticsEntity> = emptyList()
)