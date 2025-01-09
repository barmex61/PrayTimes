package com.fatih.prayertime.domain.use_case.formatted_time_use_case

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

class FormattedTimeUseCase @Inject constructor() {

    operator fun invoke() : String {
        val currentTime = Calendar.getInstance()
        val formatter = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        return formatter.format(currentTime.time)
    }
}