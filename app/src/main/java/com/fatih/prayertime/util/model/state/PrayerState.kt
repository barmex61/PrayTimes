package com.fatih.prayertime.util.model.state

import com.fatih.prayertime.domain.model.PrayTimes

data class PrayerState(
    val prayTimes: PrayTimes? = null,
    val isLoading: Boolean = false,
    val error: String? = null,

    )