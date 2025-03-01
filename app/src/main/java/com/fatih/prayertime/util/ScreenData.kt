package com.fatih.prayertime.util

import com.fatih.prayertime.presentation.main_activity.view.ResourceType

data class ScreenData(
    val title: String,
    val iconResourceType : ResourceType,
    val iconRoute: Int = 0,
    val route: String
)