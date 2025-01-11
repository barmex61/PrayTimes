package com.fatih.prayertime.domain.use_case.formatted_use_cases.formatted_time_use_case

import org.threeten.bp.LocalTime
import org.threeten.bp.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject

class FormattedTimeUseCase @Inject constructor() {

    operator fun invoke() : String {
        val currentTime = LocalTime.now()
        val formatter = DateTimeFormatter.ofPattern("HH:mm:ss", Locale.getDefault())
        return currentTime.format(formatter)
    }
}