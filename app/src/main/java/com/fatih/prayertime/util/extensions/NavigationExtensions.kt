package com.fatih.prayertime.util.extensions

import androidx.navigation.NavController

fun NavController.navigateToScreen(route: String) {
    this.navigate(route) {
        popUpTo(this@navigateToScreen.graph.startDestinationId) { saveState = false }
        launchSingleTop = true
        restoreState = false
        if (route == "qibla") this@navigateToScreen.popBackStack()
    }
} 