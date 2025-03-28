package com.fatih.prayertime.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Settings(
    val selectedTheme: ThemeOption = ThemeOption.SYSTEM_DEFAULT,
    val vibrationEnabled: Boolean = true,
    val prayerAlarms: List<PrayerAlarm> = mutableListOf(),
    val silenceWhenCuma : Boolean = true,
    val notificationDismissTime: Long = 15000,
    
    val prayerCalculationMethod: Int? = null,
    val prayerTimeTuneValues: Map<String, Int> = mapOf(
        "fajr" to 0,
        "dhuhr" to 0,
        "asr" to 0,
        "maghrib" to 0,
        "isha" to 0
    ) 
)

enum class ThemeOption { LIGHT, DARK, SYSTEM_DEFAULT }

