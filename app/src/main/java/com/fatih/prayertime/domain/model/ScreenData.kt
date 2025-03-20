package com.fatih.prayertime.domain.model

import com.fatih.prayertime.util.model.enums.PrayTimesString
import com.fatih.prayertime.util.model.enums.ResourceType

data class ScreenData(
    val title: PrayTimesString,
    val iconResourceType : ResourceType,
    val iconRoute: Int = 0,
    val painterRoute : Int? = null,
    val route: String
)
