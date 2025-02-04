package com.fatih.prayertime.presentation.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext


private val LightColorScheme = lightColorScheme(
    primary = Color(0xFFA3B82D),  // Güçlü bir yeşil tonu
    secondary = Color(0xFFBCE189),  // Gri tonlarıyla uyumlu
    tertiary = Color(0xFF899177),  // Hafif sarı-yeşil tonu
    background = Color(0xFFF9F9F9),  // Açık gri beyaz
    surface = Color(0xFFFFFFFF),
    primaryContainer =  Color(0xFFEFEFEF),
    secondaryContainer = Color(0xFFE5E5E5),
    onPrimaryContainer = Color(0xBF061D26),
    onSecondaryContainer = Color(0xBF061823),// Beyaz
    onPrimary = Color.White,  // Primary metin rengi beyaz
    onSecondary = Color.Black,  // Secondary metin rengi siyah
    onTertiary = Color.Black,  // Tertiary metin rengi siyah
    onBackground = Color(0xFF000000),  // Arka plan için siyah metin
    onSurface = Color(0xFF000000), // Surface için siyah metin
)
val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF8EC5FC),  // Daha açık mavi
    secondary = Color(0xFF5A95FF),  // Orta mavi tonu
    tertiary = Color(0xFF2A59A2),  // Daha derin mavi
    primaryContainer = Color(0xFF212121), // Beyaz // Beyaz
    secondaryContainer = Color(0xFF282727),
    onPrimaryContainer = Color(0xBFD5E5F3),
    onSecondaryContainer = Color(0xBFDFEFFF),
    background = Color(0xFF121212),  // Koyu siyah
    surface = Color(0xFF1E1E1E),  // Koyu gri
    onPrimary = Color.White,  // Beyaz metin rengi
    onSecondary = Color.Black,  // Siyah metin rengi
    onTertiary = Color.White,  // Beyaz metin rengi
    onBackground = Color(0xFFDDDDDD),  // Koyu modda daha açık gri metin
    onSurface = Color(0xFFFFFFFF),  // Surface üzerinde beyaz metin
)


@Composable
fun PrayerTimeTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
