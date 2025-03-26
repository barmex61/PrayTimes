package com.fatih.prayertime.presentation.main_screen

import com.fatih.prayertime.domain.model.PrayTimes
import com.fatih.prayertime.domain.model.Weather

data class PrayerState(
    val prayTimes: PrayTimes? = null,
    val isLoading: Boolean = false,
    val error: String? = null,

) 