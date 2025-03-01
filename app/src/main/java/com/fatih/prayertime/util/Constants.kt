package com.fatih.prayertime.util

import androidx.datastore.preferences.core.stringPreferencesKey
import com.fatih.prayertime.R
import com.fatih.prayertime.domain.model.EsmaulHusna


object Constants {
    const val BASE_URL = "https://api.aladhan.com/v1/"
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
    val screens = listOf(
        ScreenData(
            title = "Home",
            iconRoute = R.drawable.mosque_icon,
            route = "home",
            iconResourceType = ResourceType.VECTOR
        ),
        ScreenData(
            title = "Qibla",
            iconRoute = R.drawable.compass_icon,
            route = "qibla",
            iconResourceType = ResourceType.PAINTER
        ),
        ScreenData(
            title = "Utilities",
            iconRoute = R.drawable.settings_icon,
            route = "utilities",
            iconResourceType = ResourceType.VECTOR
        ),
        ScreenData(
            title = "Settings",
            iconRoute = R.drawable.settings_icon,
            route = "settings",
            iconResourceType = ResourceType.VECTOR
        ),
        ScreenData(
            title = "Prayer",
            iconRoute = R.drawable.pray,
            route = "prayer",
            iconResourceType = ResourceType.PAINTER
        ),
        ScreenData(
            title = "Quran",
            iconRoute = R.drawable.quran_icon,
            route = "quran",
            iconResourceType = ResourceType.PAINTER
        ),
        ScreenData(
            title = "Esmaul Husna",
            iconRoute = R.drawable.allah,
            route = "esmaul_husna",
            iconResourceType = ResourceType.PAINTER
        ),
        ScreenData(
            title = "Islamic Calendar",
            iconRoute = R.drawable.calendar,
            route = "islamic_calendar",
            iconResourceType = ResourceType.PAINTER
        ),
        ScreenData(
            title = "Hadith",
            iconRoute = R.drawable.hadith,
            route = "hadith",
            iconResourceType = ResourceType.PAINTER
        )
    )

}