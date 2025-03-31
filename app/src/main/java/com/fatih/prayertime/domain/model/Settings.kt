package com.fatih.prayertime.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Settings(
    val selectedTheme: ThemeOption = ThemeOption.SYSTEM_DEFAULT,
    val vibrationEnabled: Boolean = true,
    val prayerAlarms: List<PrayerAlarm> = mutableListOf(),
    val silenceWhenCuma : Boolean = true,
    val notificationDismissTime: Long = 30000,
    
    val prayerCalculationMethod: Int? = null,
    val prayerTimeTuneValues: Map<String, Int> = mapOf(
        PRAYER_TIME_FAJR to 0,
        PRAYER_TIME_DHUHR to 0,
        PRAYER_TIME_ASR to 0,
        PRAYER_TIME_MAGHRIB to 0,
        PRAYER_TIME_ISHA to 0
    ) ,
    val alarmSoundUri : String? = null
) {
    companion object {
        const val PRAYER_TIME_FAJR = "fajr"
        const val PRAYER_TIME_DHUHR = "dhuhr"
        const val PRAYER_TIME_ASR = "asr"
        const val PRAYER_TIME_MAGHRIB = "maghrib"
        const val PRAYER_TIME_ISHA = "isha"
    }
}

enum class ThemeOption { LIGHT, DARK, SYSTEM_DEFAULT }

