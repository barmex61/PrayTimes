package com.fatih.prayertime.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Settings(
    val selectedTheme: ThemeOption = ThemeOption.SYSTEM_DEFAULT,
    val vibrationEnabled: Boolean = true,
    val prayerAlarms: List<PrayerAlarm> = mutableListOf(),
    val silenceWhenCuma : Boolean = true,
    val shouldCacheAudio : Boolean = true,
)

enum class ThemeOption { LIGHT, DARK, SYSTEM_DEFAULT }

