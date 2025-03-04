package com.fatih.prayertime.domain.model

import com.fatih.prayertime.data.remote.dto.hadithdto.HadithSectionDetails

data class HadithSectionCardData(
    val section: String?,
    val details: HadithSectionDetails?
)
