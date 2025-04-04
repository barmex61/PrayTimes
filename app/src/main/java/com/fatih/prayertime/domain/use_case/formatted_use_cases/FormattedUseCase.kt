package com.fatih.prayertime.domain.use_case.formatted_use_cases

import org.threeten.bp.DayOfWeek
import org.threeten.bp.Instant
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import org.threeten.bp.LocalTime
import org.threeten.bp.ZoneId
import org.threeten.bp.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FormattedUseCase @Inject constructor() {

    private val formatterDDMMYYYY = DateTimeFormatter.ofPattern("dd-MM-yyyy", Locale.getDefault())
    private val formatterMMYYYY = DateTimeFormatter.ofPattern("MM-yyyy", Locale.getDefault())
    private val formatterDDMM = DateTimeFormatter.ofPattern("dd-MM", Locale.getDefault())
    private val formatterEEE =  DateTimeFormatter.ofPattern("d MMM, EEE", Locale.getDefault())
    private val formatterEEEE = DateTimeFormatter.ofPattern("EEEE", Locale.getDefault())

    private val formatterHHMM = DateTimeFormatter.ofPattern("HH:mm", Locale.getDefault())
    private val formatterHHMMSS = DateTimeFormatter.ofPattern("HH:mm:ss", Locale.getDefault())
    private val formatterDDMMYYYYHHMMSS = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss", Locale.getDefault())

    fun formatLocalDateToMMYYYY(localDate: LocalDate): String {
        return localDate.format(formatterMMYYYY)
    }

    fun formatOfPatternDDMMYYYY(localDate: LocalDate): String {
        return localDate.format(formatterDDMMYYYY)
    }

    fun formatDDMMYYYYtoLong(dateString: String): Long {
        return formatLocalDateToLong(formatDDMMYYYYDateToLocalDate(dateString))
    }

    fun isToday(date: String) : Boolean{
        val localDate = formatDDMMYYYYDateToLocalDate(date)
        return localDate.isEqual(LocalDate.now())
    }

    fun isFriday(dateString: String): Boolean {
        val dateTime = LocalDateTime.parse(dateString, formatterDDMMYYYYHHMMSS)
        return dateTime.dayOfWeek == DayOfWeek.FRIDAY
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

    fun formatHHMMtoLong(time: String,localDate: LocalDate): Long {
        return LocalTime.parse(time, formatterHHMM).atDate(localDate).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
    }

    fun formatHHMMtoLongWithLocalDate(time: String, localDate: LocalDate): Long {
        return LocalTime.parse(time, formatterHHMM).atDate(localDate).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
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

    fun formatLocalDateToLong(localDate: LocalDate) : Long {
        return localDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
    }

    fun formatLongToLocalDateTime(time: Long) : String {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(time), ZoneId.systemDefault()).format(formatterDDMMYYYYHHMMSS)
    }

    fun minusMinutesFromTime(time: String, minutesToAdd: Long): String {
        val localTime = LocalTime.parse(time, formatterHHMM)
        val newTime = localTime.minusMinutes(minutesToAdd)
        return newTime.format(formatterHHMM)
    }

}