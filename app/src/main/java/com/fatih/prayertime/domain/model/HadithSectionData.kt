package com.fatih.prayertime.domain.model

import com.fatih.prayertime.data.remote.dto.hadithdto.Hadith
import com.fatih.prayertime.data.remote.dto.hadithdto.HadithSectionInfo

data class HadithSectionData(
    val section: String?,
    val details: HadithSectionInfo?,
    val hadithList : List<Hadith>,
    val hadithCount : Int
)
