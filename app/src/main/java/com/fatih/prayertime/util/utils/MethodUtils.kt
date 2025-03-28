package com.fatih.prayertime.util.utils

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.fatih.prayertime.R

object MethodUtils {

    fun getCalculationMethodName(methodId: Int): String {
        return when (methodId) {
            0 -> "Shia Ithna-Ashari"
            1 -> "University of Islamic Sciences, Karachi"
            2 -> "Islamic Society of North America"
            3 -> "Muslim World League"
            4 -> "Umm Al-Qura University, Makkah"
            5 -> "Egyptian General Authority of Survey"
            7 -> "Institute of Geophysics, University of Tehran"
            8 -> "Gulf Region"
            9 -> "Kuwait"
            10 -> "Qatar"
            11 -> "Majlis Ugama Islam Singapura"
            12 -> "Union Organization Islamic de France"
            13 -> "Diyanet İşleri Başkanlığı"
            14 -> "Algeria"
            15 -> "Türkiye Takvimi"
            16 -> "Russia"
            17 -> "Moonsighting Committee Worldwide (Moonsighting.com)"
            18 -> "Dubai"
            19 -> "United Arab Emirates"
            20 -> "Jakim, Malaysia"
            21 -> "Tunisia"
            22 -> "Afghanistan"
            23 -> "Bosnia and Herzegovina"
            else -> "Bilinmeyen Metod"
        }
    }

    @Composable
    fun getCalculationMethodNameComposable(methodId: Int): String {
        return when (methodId) {
            0 -> stringResource(R.string.calculation_method_jafari)
            1 -> stringResource(R.string.calculation_method_karachi)
            2 -> stringResource(R.string.calculation_method_isna)
            3 -> stringResource(R.string.calculation_method_mwl)
            4 -> stringResource(R.string.calculation_method_makkah)
            5 -> stringResource(R.string.calculation_method_egypt)
            7 -> stringResource(R.string.calculation_method_tehran)
            8 -> stringResource(R.string.calculation_method_gulf)
            9 -> stringResource(R.string.calculation_method_kuwait)
            10 -> stringResource(R.string.calculation_method_qatar)
            11 -> stringResource(R.string.calculation_method_singapore)
            12 -> stringResource(R.string.calculation_method_france)
            13 -> stringResource(R.string.calculation_method_turkey)
            14 -> stringResource(R.string.calculation_method_russia)
            15 -> stringResource(R.string.calculation_method_moonsighting)
            16 -> stringResource(R.string.calculation_method_dubai)
            17 -> stringResource(R.string.calculation_method_malaysia)
            18 -> stringResource(R.string.calculation_method_tunisia)
            19 -> stringResource(R.string.calculation_method_algeria)
            20 -> stringResource(R.string.calculation_method_indonesia)
            21 -> stringResource(R.string.calculation_method_morocco)
            22 -> stringResource(R.string.calculation_method_portugal)
            23 -> stringResource(R.string.calculation_method_jordan)
            else -> stringResource(R.string.calculation_method_isna)
        }
    }
    
    fun getCalculationMethodNameWithContext(context: Context, methodId: Int): String {
        return when (methodId) {
            0 -> context.getString(R.string.calculation_method_jafari)
            1 -> context.getString(R.string.calculation_method_karachi)
            2 -> context.getString(R.string.calculation_method_isna)
            3 -> context.getString(R.string.calculation_method_mwl)
            4 -> context.getString(R.string.calculation_method_makkah)
            5 -> context.getString(R.string.calculation_method_egypt)
            7 -> context.getString(R.string.calculation_method_tehran)
            8 -> context.getString(R.string.calculation_method_gulf)
            9 -> context.getString(R.string.calculation_method_kuwait)
            10 -> context.getString(R.string.calculation_method_qatar)
            11 -> context.getString(R.string.calculation_method_singapore)
            12 -> context.getString(R.string.calculation_method_france)
            13 -> context.getString(R.string.calculation_method_turkey)
            14 -> context.getString(R.string.calculation_method_russia)
            15 -> context.getString(R.string.calculation_method_moonsighting)
            16 -> context.getString(R.string.calculation_method_dubai)
            17 -> context.getString(R.string.calculation_method_malaysia)
            18 -> context.getString(R.string.calculation_method_tunisia)
            19 -> context.getString(R.string.calculation_method_algeria)
            20 -> context.getString(R.string.calculation_method_indonesia)
            21 -> context.getString(R.string.calculation_method_morocco)
            22 -> context.getString(R.string.calculation_method_portugal)
            23 -> context.getString(R.string.calculation_method_jordan)
            else -> context.getString(R.string.calculation_method_isna)
        }
    }
}