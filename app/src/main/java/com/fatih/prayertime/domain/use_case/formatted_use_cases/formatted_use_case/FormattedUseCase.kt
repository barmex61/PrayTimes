package com.fatih.prayertime.domain.use_case.formatted_use_cases.formatted_use_case

import org.threeten.bp.Instant
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId
import org.threeten.bp.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject

class FormattedUseCase @Inject constructor() {

    private val formatterDDMMYYYY = DateTimeFormatter.ofPattern("dd-MM-yyyy", Locale.getDefault())
    private val formatterDDMM = DateTimeFormatter.ofPattern("dd-MM", Locale.getDefault())

    private val formatterHHMMSS = DateTimeFormatter.ofPattern("HH:mm:ss", Locale.getDefault())
    private val formatterDDMMYYYYHHMM = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm", Locale.getDefault())

    fun formatOfPatternDDMMYYYY(localDate: LocalDate): String {
        return localDate.format(formatterDDMMYYYY)
    }

    fun formatOfPatternDDMM(localDate: LocalDate): String{
        return localDate.format(formatterDDMM)
    }

    fun formatDDMMYYYYHHMMDateToLocalDateTime(date: String): LocalDateTime {
        return LocalDateTime.parse(date, formatterDDMMYYYYHHMM)
    }

    fun formatDDMMYYYYDateToLocalDate(date: String): LocalDate {
        return LocalDate.parse(date, formatterDDMMYYYY)
    }

    fun isLastDayOfMonth(localDate: LocalDate): Boolean {
        return localDate.dayOfMonth == localDate.lengthOfMonth()
    }



    fun formatHHMMSS(localDateTime: LocalDateTime) : String {
        return localDateTime.format(formatterHHMMSS)
    }

    fun formatLocalDateTimeToLong(localDateTime: LocalDateTime) : Long {
        return localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
    }

    fun formatLongToLocalDateTime(time: Long) : String {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(time), ZoneId.systemDefault()).format(formatterDDMMYYYYHHMM)
    }

}