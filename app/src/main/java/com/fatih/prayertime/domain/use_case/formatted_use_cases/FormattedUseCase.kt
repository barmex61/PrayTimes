package com.fatih.prayertime.domain.use_case.formatted_use_cases

import org.threeten.bp.Instant
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import org.threeten.bp.LocalTime
import org.threeten.bp.ZoneId
import org.threeten.bp.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject

class FormattedUseCase @Inject constructor() {

    private val formatterDDMMYYYY = DateTimeFormatter.ofPattern("dd-MM-yyyy", Locale.getDefault())
    private val formatterDDMM = DateTimeFormatter.ofPattern("dd-MM", Locale.getDefault())
    private val formatterEEE =  DateTimeFormatter.ofPattern("d MMM, EEE", Locale.getDefault())
    private val formatterEEEE = DateTimeFormatter.ofPattern("EEEE", Locale.getDefault())

    private val formatterHHMM = DateTimeFormatter.ofPattern("HH:mm", Locale.getDefault())
    private val formatterHHMMSS = DateTimeFormatter.ofPattern("HH:mm:ss", Locale.getDefault())
    private val formatterDDMMYYYYHHMMSS = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss", Locale.getDefault())

    fun formatOfPatternDDMMYYYY(localDate: LocalDate): String {
        return localDate.format(formatterDDMMYYYY)
    }

    fun formatOfPatternDDMM(localDate: LocalDate): String{
        return localDate.format(formatterDDMM)
    }

    fun formatOfPatternEEE(localDate: LocalDate): String{
        return localDate.format(formatterEEE)
    }

    fun formatOfPatternEEEE(localDate: LocalDate): String{
        return localDate.format(formatterEEEE)
    }

    fun formatDDMMYYYYHHMMDateToLocalDateTime(date: String): LocalDateTime {
        return LocalDateTime.parse(date, formatterDDMMYYYYHHMMSS)
    }

    fun formatDDMMYYYYDateToLocalDate(date: String): LocalDate {
        return LocalDate.parse(date, formatterDDMMYYYY)
    }

    fun formatHHMMtoLong(time: String): Long {
        return LocalTime.parse(time, formatterHHMM).atDate(LocalDate.now()).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
    }

    fun formatHHMM(localDateTime: LocalDateTime) : String {
        return localDateTime.format(formatterHHMM)
    }

    fun formatHHMMSS(localDateTime: LocalDateTime) : String {
        return localDateTime.format(formatterHHMMSS)
    }

    fun formatLocalDateTimeToLong(localDateTime: LocalDateTime) : Long {
        return localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
    }

    fun formatLongToLocalDateTime(time: Long) : String {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(time), ZoneId.systemDefault()).format(formatterDDMMYYYYHHMMSS)
    }

}