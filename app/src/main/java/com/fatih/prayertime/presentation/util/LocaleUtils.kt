package com.fatih.prayertime.presentation.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import java.util.Locale


@Composable
fun isTurkishLocale(): Boolean {
    val currentLocale = LocalConfiguration.current.locales[0]
    return currentLocale.language == "tr"
}


@Composable
fun getLocalizedString(englishText: String?, turkishText: String?): String {
    if (englishText.isNullOrBlank() && turkishText.isNullOrBlank()) {
        return ""
    }
    
    return when {
        isTurkishLocale() -> turkishText ?: englishText ?: ""
        else -> englishText ?: turkishText ?: ""
    }
} 