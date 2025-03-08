package com.fatih.prayertime.util

import androidx.compose.ui.graphics.Color
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.fatih.prayertime.R
import com.fatih.prayertime.data.remote.dto.duadto.DuaCategory
import com.fatih.prayertime.domain.model.EsmaulHusna
import com.fatih.prayertime.domain.model.PrayCategoryTr


object Constants {

    const val ALADHAN_API_BASE_URL = "https://api.aladhan.com/v1/"
    const val HADITH_API_BASE_URL = "https://cdn.jsdelivr.net/gh/fawazahmed0/hadith-api@1/"
    const val DUA_API_BASE_URL = "https://dua-dhikr.onrender.com"
    const val TUNE = "0,-0,-7,7,6,7,7,0,0"
    val prayApiMethods = hashMapOf(
        "Jafari / Shia Ithna-Ashari" to 0,
        "University of Islamic Sciences, Karachi" to 1,
        "Islamic Society of North America" to 2,
        "Muslim World League" to 3,
        "Umm Al-Qura University, Makkah" to 4,
        "Egyptian General Authority of Survey" to 5,
        "Institute of Geophysics, University of Tehran" to 7,
        "Gulf Region" to 8,
        "Kuwait" to 9,
        "Qatar" to 10,
        "Majlis Ugama Islam Singapura, Singapore" to 12,
        "Union Organization islamic de France" to 12,
        "Diyanet İşleri Başkanlığı, Turkey" to 13,
        "Spiritual Administration of Muslims of Russia" to 14,
        "Moonsighting Committee Worldwide (also requires shafaq parameter)" to 15,
        "Dubai (experimental)" to 16,
        "Jabatan Kemajuan Islam Malaysia (JAKIM)" to 17,
        "Tunisia" to 18,
        "Algeria" to 19,
        "KEMENAG - Kementerian Agama Republik Indonesia" to 20,
        "Morocco" to 21,
        "Comunidade Islamica de Lisboa" to 22,
        "Ministry of Awqaf, Islamic Affairs and Holy Places, Jordan" to 23
    )
    val islamicCalendarMethods = listOf("DIYANET","HJCoSA","UAQ","MATHEMATICAL")
    val selectedIslamicCalendarMethod = islamicCalendarMethods[0]
    var esmaulHusnaList: List<EsmaulHusna> = emptyList()
    val SETTINGS_KEY = stringPreferencesKey("settings_json")
    var duaCategory : DuaCategory? = null
    val colors = listOf(
        Color(0xFFFF6632), // Red
        Color(0xFF0000FF), // Blue
        Color(0xFFFFFF00), // Yellow
        Color(0xFFFF00FF), // Magenta
        Color(0xFF00FFFF), // Cyan
        Color(0xFFFFA500), // Orange
        Color(0xFF800080), // Purple
        Color(0xFF008000), // Dark Green
        Color(0xFF007C98), // Navy
        Color(0xFFFFC0CB), // Pink
        Color(0xFFE07979), // Brown
        Color(0xFF808080), // Gray
        Color(0xFF00FF7F), // Spring Green
        Color(0xFF4682B4), // Steel Blue
        Color(0xFFD2691E), // Chocolate
        Color(0xFF9ACD32), // Yellow Green
        Color(0xFF8A2BE2), // Blue Violet
        Color(0xFF5F9EA0), // Cadet Blue
    )
    val screens = listOf(
        ScreenData(
            title = PrayTimesString.Home,
            route = "home",
            iconRoute = R.drawable.home_icon,
            arguments = emptyList()
        ),
        ScreenData(
            title = PrayTimesString.Qibla,
            route = "qibla",
            iconRoute = R.drawable.compass_icon,
            arguments = emptyList()
        ),
        ScreenData(
            title = PrayTimesString.Utilities,
            route = "utilities",
            iconRoute = R.drawable.utilities_icon,
            arguments = emptyList()
        ),
        ScreenData(
            title = PrayTimesString.Settings,
            route = "settings",
            iconRoute = R.drawable.settings_icon,
            arguments = emptyList()
        ),
        ScreenData(
            title = PrayTimesString.ESMAUL_HUSNA,
            route = "esmaulhusna",
            iconRoute = R.drawable.esmaul_husna_icon,
            arguments = emptyList()
        ),
        ScreenData(
            title = PrayTimesString.ISLAMIC_CALENDAR,
            route = "calendar",
            iconRoute = R.drawable.calendar_icon,
            arguments = emptyList()
        ),
        ScreenData(
            title = PrayTimesString.HADITH,
            route = "hadith",
            iconRoute = R.drawable.hadith_icon,
            arguments = emptyList()
        ),
        ScreenData(
            title = PrayTimesString.HADITH_COLLECTION,
            route = "hadith_collection/{collectionPath}",
            iconRoute = R.drawable.hadith_collection_icon,
            arguments = listOf(
                navArgument("collectionPath") { type = NavType.StringType }
            )
        ),
        ScreenData(
            title = PrayTimesString.HADITH_SECTION_DETAILS,
            route = "hadith_section_details",
            iconRoute = R.drawable.hadith_details_icon,
            arguments = emptyList()
        ),
        ScreenData(
            title = PrayTimesString.PRAYER,
            route = "prayer",
            iconRoute = R.drawable.prayer_icon,
            arguments = emptyList()
        ),
        ScreenData(
            title = PrayTimesString.PRAY_CATEGORY_DETAILS,
            route = "pray_category_details/{categoryIndex}",
            iconRoute = R.drawable.prayer_details_icon,
            arguments = listOf(
                navArgument("categoryIndex") { type = NavType.IntType }
            )
        ),
        ScreenData(
            title = PrayTimesString.PRAYER_DETAIL,
            route = "prayer_detail",
            iconRoute = R.drawable.prayer_detail_icon,
            arguments = emptyList()
        ),
        ScreenData(
            title = PrayTimesString.FAVORITES,
            route = "favorites",
            iconRoute = R.drawable.favorite_icon,
            arguments = emptyList()
        ),
        ScreenData(
            title = PrayTimesString.STATISTICS,
            route = "statistics",
            iconRoute = R.drawable.statistics_icon,
            arguments = emptyList()
        )
    )

}