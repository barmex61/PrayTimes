package com.fatih.prayertime.domain.use_case.formatted_use_cases.formatted_date_use_case

import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject

class FormattedDateUseCase @Inject constructor() {

    operator fun invoke(): String {
        val currentDate = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy", Locale.getDefault())
        return currentDate.format(formatter)
    }

}