package com.fatih.namazvakitleri.domain.use_case.formatted_date_use_case

import android.text.format.DateFormat
import java.util.Date
import javax.inject.Inject

class FormattedDateUseCase @Inject constructor() {

    operator fun invoke(): String {
        val date = Date()
        return DateFormat.format("dd MMMM yyyy", date).toString()
    }

}