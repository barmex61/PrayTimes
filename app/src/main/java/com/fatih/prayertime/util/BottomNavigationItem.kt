package com.fatih.prayertime.util

import androidx.compose.ui.graphics.vector.ImageVector
import com.fatih.prayertime.presentation.main_activity.view.ResourceType

data class BottomNavigationItem(
    val title: String,
    val iconResourceType : ResourceType,
    val iconRoute: Int = 0,
    val route: String
)