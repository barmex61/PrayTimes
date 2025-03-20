package com.fatih.prayertime.util.config

import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.fatih.prayertime.R
import com.fatih.prayertime.domain.model.ScreenData
import com.fatih.prayertime.util.model.enums.PrayTimesString
import com.fatih.prayertime.util.model.enums.ResourceType

object NavigationConfig {
    val screens = listOf(
        ScreenData(
            title = PrayTimesString.Home,
            iconRoute = R.drawable.mosque_icon,
            route = "home",
            iconResourceType = ResourceType.VECTOR
        ),
        ScreenData(
            title = PrayTimesString.Qibla,
            iconRoute = R.drawable.compass_icon,
            painterRoute = R.drawable.compass,
            route = "qibla",
            iconResourceType = ResourceType.PAINTER
        ),
        ScreenData(
            title = PrayTimesString.Settings,
            iconRoute = R.drawable.settings_icon,
            route = "settings",
            iconResourceType = ResourceType.VECTOR
        ),
        ScreenData(
            title = PrayTimesString.Utilities,
            iconRoute = R.drawable.baseline_menu_24,
            route = "utilities",
            iconResourceType = ResourceType.VECTOR
        ),
        ScreenData(
            title = PrayTimesString.PRAY_CATEGORY_DETAILS,
            iconRoute = R.drawable.pray,
            route = "duaCategoriesDetail",
            iconResourceType = ResourceType.PAINTER,
        ),
        ScreenData(
            title = PrayTimesString.HADITH_SECTION_DETAILS,
            iconRoute = R.drawable.hadith,
            route = "hadithSectionDetailScreen/{collectionPath}/{hadithSectionIndex}/{hadithIndex}",
            iconResourceType = ResourceType.PAINTER,
        ),
        ScreenData(
            title = PrayTimesString.HADITH_COLLECTION,
            iconRoute = R.drawable.hadith,
            route = "hadithCollections/{collectionPath}",
            iconResourceType = ResourceType.PAINTER,
            arguments = listOf(
                navArgument("collectionPath"){type = NavType.StringType}
            )
        ),
        ScreenData(
            title = PrayTimesString.PRAYER_DETAIL,
            iconRoute = R.drawable.pray,
            route = "duaDetail/{duaId}/{categoryId}",
            iconResourceType = ResourceType.PAINTER,
        ),
        ScreenData(
            title = PrayTimesString.PRAYER,
            iconRoute = R.drawable.pray,
            route = "duaCategories",
            iconResourceType = ResourceType.PAINTER,
        ),
        ScreenData(
            title = PrayTimesString.QURAN,
            iconRoute = R.drawable.quran_icon,
            painterRoute = R.drawable.quran,
            route = "quran",
            iconResourceType = ResourceType.PAINTER
        ),
        ScreenData(
            title = PrayTimesString.ESMAUL_HUSNA,
            iconRoute = R.drawable.allah,
            route = "esmaul_husna",
            iconResourceType = ResourceType.PAINTER
        ),
        ScreenData(
            title = PrayTimesString.ISLAMIC_CALENDAR,
            iconRoute = R.drawable.calendar,
            route = "islamic_calendar",
            iconResourceType = ResourceType.PAINTER
        ),
        ScreenData(
            title = PrayTimesString.HADITH,
            iconRoute = R.drawable.hadith,
            route = "hadith",
            iconResourceType = ResourceType.PAINTER
        ),
        ScreenData(
            title = PrayTimesString.FAVORITES,
            route = "favorites",
            iconRoute = R.drawable.favorite_icon,
            iconResourceType = ResourceType.VECTOR
        ),
        ScreenData(
            title = PrayTimesString.STATISTICS,
            route = "statistics",
            iconRoute = R.drawable.statistics_icon,
            iconResourceType = ResourceType.VECTOR
        ),
        ScreenData(
            title = PrayTimesString.QURAN_DETAIL_SCREEN,
            route = "quran_detail/{surahNumber}",
            iconRoute = R.drawable.quran,
            iconResourceType = ResourceType.VECTOR
        ),
        ScreenData(
            title = PrayTimesString.QURAN_JUZ_DETAIL_SCREEN,
            route = "quran_juz_detail/{juzNumber}",
            iconRoute = R.drawable.quran,
            iconResourceType = ResourceType.VECTOR
        )
    )
} 