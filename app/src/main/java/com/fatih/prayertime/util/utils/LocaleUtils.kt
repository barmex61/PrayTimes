package com.fatih.prayertime.util.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration


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