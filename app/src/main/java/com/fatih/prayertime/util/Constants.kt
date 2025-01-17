package com.fatih.prayertime.util

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Face
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object Constants {
    const val BASE_URL = "https://api.aladhan.com/v1/"
    val bottomNavItems = listOf(
        BottomNavigationItem(
            title = "Home",
            icon = Icons.Outlined.Face,
            route = "home"
        ),
        BottomNavigationItem(
            title = "Settings",
            icon = Icons.Outlined.Face,
            route = "settings"
        ),
        BottomNavigationItem(
            title = "Profile",
            icon = Icons.Outlined.Face,
            route = "profile"
        ),
        BottomNavigationItem(
            title = "About",
            icon = Icons.Outlined.Face,
            route = "about"
        ),
        BottomNavigationItem(
            title = "Contact",
            icon = Icons.Outlined.Face,
            route = "contact"
        )
    )

    val MIGRATION_2_3 = object : Migration(2, 3) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("DROP TABLE IF EXISTS `alarmInfo`")
        }
    }
}