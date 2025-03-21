package com.fatih.prayertime.util.config

object ApiConfig {
    const val ALADHAN_API_BASE_URL = "https://api.aladhan.com/v1/"
    const val HADITH_API_BASE_URL = "https://cdn.jsdelivr.net/gh/fawazahmed0/hadith-api@1/"
    const val QURAN_API_BASE_URL = "https://api.alquran.cloud/v1/"
    const val BASE_AUDIO_URL = "https://cdn.islamic.network/quran/"
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
} 