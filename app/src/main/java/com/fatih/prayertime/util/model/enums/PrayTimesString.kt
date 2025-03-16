package com.fatih.prayertime.util.model.enums

import com.fatih.prayertime.R

enum class PrayTimesString(val stringResId: Int) {
    Morning(R.string.morning),
    Noon(R.string.noon),
    Afternoon(R.string.afternoon),
    Evening(R.string.evening),
    Night(R.string.night),
    Home(R.string.home),
    Qibla(R.string.qibla),
    Settings(R.string.settings),
    Utilities(R.string.utilities),
    LIGHT(R.string.light),
    DARK(R.string.dark),
    SYSTEM_DEFAULT(R.string.system_default),
    PRAY_CATEGORY_DETAILS(R.string.pray_category_details),
    HADITH_SECTION_DETAILS(R.string.hadith_section_detail),
    QURAN(R.string.quran),
    ESMAUL_HUSNA(R.string.esmaul_husna),
    ISLAMIC_CALENDAR(R.string.islamic_calendar),
    PRAYER(R.string.prayer),
    PRAYER_DETAIL(R.string.prayer_detail),
    HADITH_COLLECTION(R.string.hadith_collection),
    FAVORITES(R.string.favorites),
    STATISTICS(R.string.statistics),
    QURAN_DETAIL_SCREEN(R.string.quran_detail),
    HADITH(R.string.hadith);

    companion object {
        fun fromString(str: String): PrayTimesString {
            return try {
                valueOf(str)
            } catch (e: IllegalArgumentException) {
                throw e
            }
        }
    }
} 