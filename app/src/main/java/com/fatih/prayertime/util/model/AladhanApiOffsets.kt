package com.fatih.prayertime.util.model


object AladhanApiOffsets {

    fun getDefaultOffsets(calculationMethod: Int): Map<String, Int> {
        return when (calculationMethod) {
            // Farklı hesaplama metodları için offset değerleri
            
            // 0 - Jafari / Shia Ithna-Ashari
            0 -> mapOf(
                "imsak" to 0,
                "fajr" to 0,
                "sunrise" to 0,
                "dhuhr" to 0,
                "asr" to 0, 
                "sunset" to 0,
                "maghrib" to 0,
                "isha" to 0,
                "midnight" to 0
            )
            
            // 1 - University of Islamic Sciences, Karachi
            1 -> mapOf(
                "imsak" to 0,
                "fajr" to 0,
                "sunrise" to 0,
                "dhuhr" to 0,
                "asr" to 0, 
                "sunset" to 0,
                "maghrib" to 0,
                "isha" to 0,
                "midnight" to 0
            )
            
            // 2 - Islamic Society of North America (ISNA)
            2 -> mapOf(
                "imsak" to 0,
                "fajr" to 0,
                "sunrise" to 0,
                "dhuhr" to 0,
                "asr" to 0, 
                "sunset" to 0,
                "maghrib" to 0,
                "isha" to 0,
                "midnight" to 0
            )
            
            // 3 - Muslim World League (MWL)
            3 -> mapOf(
                "imsak" to 0,
                "fajr" to 0,
                "sunrise" to 0,
                "dhuhr" to 0,
                "asr" to 0, 
                "sunset" to 0,
                "maghrib" to 0,
                "isha" to 0,
                "midnight" to 0
            )
            
            // 4 - Umm Al-Qura University, Makkah
            4 -> mapOf(
                "imsak" to 0,
                "fajr" to 0,
                "sunrise" to 0,
                "dhuhr" to 0,
                "asr" to 0, 
                "sunset" to 0,
                "maghrib" to 0,
                "isha" to 0,
                "midnight" to 0
            )
            
            // 5 - Egyptian General Authority of Survey
            5 -> mapOf(
                "imsak" to 0,
                "fajr" to 0,
                "sunrise" to 0,
                "dhuhr" to 0,
                "asr" to 0, 
                "sunset" to 0,
                "maghrib" to 0,
                "isha" to 0,
                "midnight" to 0
            )
            
            // 7 - Institute of Geophysics, University of Tehran
            7 -> mapOf(
                "imsak" to 0,
                "fajr" to 0,
                "sunrise" to 0,
                "dhuhr" to 0,
                "asr" to 0, 
                "sunset" to 0,
                "maghrib" to 0,
                "isha" to 0,
                "midnight" to 0
            )
            
            // 8 - Gulf Region
            8 -> mapOf(
                "imsak" to 0,
                "fajr" to 0,
                "sunrise" to 0,
                "dhuhr" to 0,
                "asr" to 0, 
                "sunset" to 0,
                "maghrib" to 0,
                "isha" to 0,
                "midnight" to 0
            )
            
            // 9 - Kuwait
            9 -> mapOf(
                "imsak" to 0,
                "fajr" to 0,
                "sunrise" to 0,
                "dhuhr" to 0,
                "asr" to 0, 
                "sunset" to 0,
                "maghrib" to 0,
                "isha" to 0,
                "midnight" to 0
            )
            
            // 10 - Qatar
            10 -> mapOf(
                "imsak" to 0,
                "fajr" to 0,
                "sunrise" to 0,
                "dhuhr" to 0,
                "asr" to 0, 
                "sunset" to 0,
                "maghrib" to 0,
                "isha" to 0,
                "midnight" to 0
            )
            
            // 11 - Majlis Ugama Islam Singapura, Singapore
            11 -> mapOf(
                "imsak" to 0,
                "fajr" to 0,
                "sunrise" to 0,
                "dhuhr" to 0,
                "asr" to 0, 
                "sunset" to 0,
                "maghrib" to 0,
                "isha" to 0,
                "midnight" to 0
            )
            
            // 12 - Union Organization islamic de France
            12 -> mapOf(
                "imsak" to 0,
                "fajr" to 0,
                "sunrise" to 0,
                "dhuhr" to 0,
                "asr" to 0, 
                "sunset" to 0,
                "maghrib" to 0,
                "isha" to 0,
                "midnight" to 0
            )
            
            // 13 - Diyanet İşleri Başkanlığı, Turkey
            // Türkiye için Diyanet İşleri'nin güvenilir offset değerleri
            13 -> mapOf(
                "imsak" to 0,
                "fajr" to 0,
                "sunrise" to -7,
                "dhuhr" to 5,
                "asr" to 4, 
                "sunset" to 7,
                "maghrib" to 7,
                "isha" to 0,
                "midnight" to 0
            )
            
            // 14 - Spiritual Administration of Muslims of Russia
            14 -> mapOf(
                "imsak" to 0,
                "fajr" to 0,
                "sunrise" to 0,
                "dhuhr" to 0,
                "asr" to 0, 
                "sunset" to 0,
                "maghrib" to 0,
                "isha" to 0,
                "midnight" to 0
            )
            
            // 15 - Moonsighting Committee Worldwide
            15 -> mapOf(
                "imsak" to 0,
                "fajr" to 0,
                "sunrise" to 0,
                "dhuhr" to 0,
                "asr" to 0, 
                "sunset" to 0,
                "maghrib" to 0,
                "isha" to 0,
                "midnight" to 0
            )
            
            // 16 - Dubai (experimental)
            16 -> mapOf(
                "imsak" to 0,
                "fajr" to 0,
                "sunrise" to 0,
                "dhuhr" to 3,
                "asr" to 0, 
                "sunset" to 3,
                "maghrib" to 3,
                "isha" to 0,
                "midnight" to 0
            )
            
            // 17 - Jabatan Kemajuan Islam Malaysia (JAKIM)
            17 -> mapOf(
                "imsak" to 0,
                "fajr" to 0,
                "sunrise" to 0,
                "dhuhr" to 0,
                "asr" to 0, 
                "sunset" to 0,
                "maghrib" to 0,
                "isha" to 0,
                "midnight" to 0
            )
            
            // 18 - Tunisia
            18 -> mapOf(
                "imsak" to 0,
                "fajr" to 0,
                "sunrise" to 0,
                "dhuhr" to 0,
                "asr" to 0, 
                "sunset" to 0,
                "maghrib" to 0,
                "isha" to 0,
                "midnight" to 0
            )
            
            // 19 - Algeria
            19 -> mapOf(
                "imsak" to 0,
                "fajr" to 0,
                "sunrise" to 0,
                "dhuhr" to 0,
                "asr" to 0, 
                "sunset" to 0,
                "maghrib" to 0,
                "isha" to 0,
                "midnight" to 0
            )
            
            // 20 - KEMENAG - Kementerian Agama Republik Indonesia
            20 -> mapOf(
                "imsak" to 0,
                "fajr" to 0,
                "sunrise" to 0,
                "dhuhr" to 0,
                "asr" to 0, 
                "sunset" to 0,
                "maghrib" to 0,
                "isha" to 0,
                "midnight" to 0
            )
            
            // 21 - Morocco
            21 -> mapOf(
                "imsak" to 0,
                "fajr" to 0,
                "sunrise" to 0,
                "dhuhr" to 5,
                "asr" to 0, 
                "sunset" to 0,
                "maghrib" to 5,
                "isha" to 0,
                "midnight" to 0
            )
            
            // 22 - Comunidade Islamica de Lisboa
            22 -> mapOf(
                "imsak" to 0,
                "fajr" to 0,
                "sunrise" to 0,
                "dhuhr" to 5,
                "asr" to 0, 
                "sunset" to 0,
                "maghrib" to 0,
                "isha" to 0,
                "midnight" to 0
            )
            
            // 23 - Ministry of Awqaf, Islamic Affairs and Holy Places, Jordan
            23 -> mapOf(
                "imsak" to 0,
                "fajr" to 0,
                "sunrise" to 0,
                "dhuhr" to 0,
                "asr" to 0, 
                "sunset" to 0,
                "maghrib" to 0,
                "isha" to 0,
                "midnight" to 0
            )
            
            else -> mapOf(
                "imsak" to 0,
                "fajr" to 0,
                "sunrise" to 0,
                "dhuhr" to 0,
                "asr" to 0, 
                "sunset" to 0,
                "maghrib" to 0,
                "isha" to 0,
                "midnight" to 0
            )
        }
    }

    fun formatTuneString(offsets: Map<String, Int>): String {
        return "${offsets["imsak"] ?: 0},${offsets["fajr"] ?: 0},${offsets["sunrise"] ?: 0},${offsets["dhuhr"] ?: 0},${offsets["asr"] ?: 0},${offsets["maghrib"] ?: 0},${offsets["sunset"] ?: 0},${offsets["isha"] ?: 0},${offsets["midnight"] ?: 0}"
    }
} 