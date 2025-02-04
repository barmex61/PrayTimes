package com.fatih.prayertime.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Settings(
    val selectedTheme: ThemeOption = ThemeOption.SYSTEM_DEFAULT,
    val vibrationEnabled: Boolean = true,
    val prayerNotifications: List<PrayerNotification> = listOf(
        PrayerNotification("Sabah Namazı", true, 30),
        PrayerNotification("Öğle Namazı", true, 30),
        PrayerNotification("İkindi Namazı", false, 30),
        PrayerNotification("Akşam Namazı", false, 30),
        PrayerNotification("Yatsı Namazı", true, 30)
    ),
    val silenceWhenCuma : Boolean = true
)
@Serializable
data class PrayerNotification(val name: String, val enabled: Boolean, val offset: Int)

enum class ThemeOption { LIGHT, DARK, SYSTEM_DEFAULT }

