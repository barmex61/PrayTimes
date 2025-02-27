package com.fatih.prayertime.presentation.main_activity.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height

import androidx.compose.foundation.layout.padding

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Face
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemColors
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext

import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.fatih.prayertime.R
import com.fatih.prayertime.domain.model.ThemeOption
import com.fatih.prayertime.domain.use_case.alarm_use_cases.ScheduleDailyAlarmUpdateUseCase
import com.fatih.prayertime.domain.use_case.formatted_use_cases.FormattedUseCase
import com.fatih.prayertime.presentation.compass_screen.view.CompassScreen
import com.fatih.prayertime.presentation.main_activity.viewmodel.AppViewModel
import com.fatih.prayertime.presentation.main_screen.view.MainScreen
import com.fatih.prayertime.presentation.settings_screen.view.SettingsScreen
import com.fatih.prayertime.presentation.settings_screen.view.SwitchSettingItem
import com.fatih.prayertime.presentation.ui.theme.PrayerTimeTheme
import com.fatih.prayertime.util.BottomNavigationItem
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    companion object {
        const val TAG = "MainActivity"
    }

    @Inject
    lateinit var formattedUseCase: FormattedUseCase

    @Inject
    lateinit var scheduleDailyAlarmUpdateUseCase: ScheduleDailyAlarmUpdateUseCase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val appViewModel: AppViewModel = hiltViewModel()
            val settings by appViewModel.settingsState.collectAsState()
            val darkTheme = when(settings.selectedTheme){
                ThemeOption.DARK -> true
                ThemeOption.LIGHT -> false
                ThemeOption.SYSTEM_DEFAULT -> isSystemInDarkTheme()
            }
            UpdateSystemBars(darkTheme)
            PrayerTimeTheme (darkTheme = darkTheme ){
                val navController = rememberNavController()
                val bottomNavItems = bottomNavItems()
                val powerSavingState by appViewModel.powerSavingState.collectAsState()
                val isLocationPermissionGranted by appViewModel.isLocationPermissionGranted.collectAsState()
                var isCalculating by remember { mutableStateOf(true) }
                val permissionLauncher =
                    rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
                        appViewModel.onLocationPermissionResult(permissions, this)
                        isCalculating = false
                    }
                val resultLauncher =
                    rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { appViewModel.checkLocationPermission() }

                LaunchedEffect(key1 = Unit) {
                    appViewModel.checkLocationPermission()
                    appViewModel.checkPowerSavingMode()
                    if (!isLocationPermissionGranted) {
                        permissionLauncher.launch(appViewModel.locationPermissions)
                        isCalculating = true
                    }
                }

                Scaffold(
                    snackbarHost = {
                        if (!isLocationPermissionGranted && !isCalculating) {
                            Snackbar(
                                action = {
                                    Button(onClick = {
                                        val intent =
                                            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                                data = Uri.fromParts("package", packageName, null)
                                            }
                                        resultLauncher.launch(intent)
                                    }) {
                                        Text("Give")
                                    }
                                }
                            ) {
                                Text("You need to give permission for this app")
                            }
                        }
                    },
                    bottomBar = {
                        BottomAppBar(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.clip(RoundedCornerShape(30.dp)),
                            tonalElevation = 10.dp,
                        ) {
                            var selectedItemIndex by rememberSaveable { mutableIntStateOf(0) }
                            bottomNavItems.forEachIndexed { index, item ->
                                NavigationBarItem(
                                    icon = { Icon(item.icon, contentDescription = item.title) },
                                    label = { Text(item.title) },
                                    selected = selectedItemIndex == index,
                                    colors = NavigationBarItemColors(
                                        selectedIconColor = MaterialTheme.colorScheme.primary,
                                        selectedTextColor = MaterialTheme.colorScheme.primary,
                                        unselectedIconColor = MaterialTheme.colorScheme.onBackground,
                                        unselectedTextColor = MaterialTheme.colorScheme.onBackground,
                                        selectedIndicatorColor = Color.Transparent,
                                        disabledIconColor = Color.Gray,
                                        disabledTextColor = Color.Gray,
                                    ),
                                    onClick = {
                                        selectedItemIndex = index
                                        val currentScreen = navController.currentBackStackEntry?.destination?.route

                                        if (currentScreen != item.title) {
                                            navController.navigate(item.title) {
                                                popUpTo(navController.graph.startDestinationId) {
                                                    inclusive = false
                                                    saveState = false
                                                }

                                                launchSingleTop = true
                                                restoreState = false // Ekran state'ini saklama, her seferinde baştan oluştur
                                            }
                                        }
                                    }
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .padding(
                                15.dp,
                                top = innerPadding.calculateTopPadding() ,
                                15.dp,
                                0.dp
                            )
                            .background(MaterialTheme.colorScheme.background)
                        ,
                    )
                    {
                        if (powerSavingState == true) {
                            showBatteryOptimizationDialog()
                        }
                        NavHost(
                            navController = navController,
                            startDestination = bottomNavItems.first().title
                        ) {
                            bottomNavItems.forEach { item ->
                                composable(
                                    route = item.title,
                                ) {
                                    when (item.title) {
                                        "Home" -> {
                                            MainScreen(appViewModel,innerPadding.calculateBottomPadding())
                                        }

                                        "Qibla" -> {
                                            CompassScreen(innerPadding.calculateBottomPadding())
                                        }

                                        "Profile" -> {
                                            //ProfileScreen()
                                        }

                                        "About" -> {
                                            //AboutScreen()
                                        }

                                        "Settings" -> {
                                            SettingsScreen(innerPadding.calculateBottomPadding())
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            val workInfos =
                WorkManager.getInstance(applicationContext).getWorkInfosByTagLiveData("AlarmWorker")

            workInfos.observe(this) { workInfoList ->
                if (!workInfoList.any { it.state == WorkInfo.State.ENQUEUED || it.state == WorkInfo.State.RUNNING }) {
                    scheduleDailyAlarmUpdateUseCase.execute(this)
                }
                workInfoList.forEach { workInfo ->
                    Log.d(TAG, "WorkInfo ID: ${workInfo.id}")
                    Log.d(TAG, "State: ${workInfo.state}")
                    Log.d(TAG, "Next Schedule Time: ${formattedUseCase.formatLongToLocalDateTime(workInfo.nextScheduleTimeMillis)}")
                }
            }
            val doubleBackToExit = remember { mutableStateOf(false) }
            val context = LocalContext.current
            BackHandler(true) {
                if (doubleBackToExit.value) {
                    finish()
                } else {
                    doubleBackToExit.value = true
                    Toast.makeText(context, "Press back again to exit", Toast.LENGTH_SHORT).show()
                    Handler(Looper.getMainLooper()).postDelayed({
                        doubleBackToExit.value = false
                    }, 2000)
                }
            }
        }
    }

    private fun showBatteryOptimizationDialog() {
        AlertDialog.Builder(this)
            .setTitle("MIUI Battery Optimization")
            .setMessage("Xiaomi model telefonlarda bildirimlerin düzgün çalışabilmesi için arkaplandaki kısıtlamaları iptal etmeniz gerekmektedir.Ayarlardan arkaplanda pil tasarrufunu devre dışı bırakın. ")
            .setPositiveButton("Settings") { _, _ ->
                val intent = Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS)
                startActivity(intent)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
@Composable
fun ComponentActivity.UpdateSystemBars(isDarkMode: Boolean) {
    val statusBarStyle = if (isDarkMode) {
        SystemBarStyle.dark(android.graphics.Color.TRANSPARENT)
    } else {
        SystemBarStyle.light(android.graphics.Color.TRANSPARENT, android.graphics.Color.TRANSPARENT)
    }

    SideEffect {
        enableEdgeToEdge(
            statusBarStyle = statusBarStyle,
            navigationBarStyle = SystemBarStyle.light(
                android.graphics.Color.TRANSPARENT,
                android.graphics.Color.TRANSPARENT
            )
        )
    }
}
@Composable
fun bottomNavItems() = listOf(
    BottomNavigationItem(
        title = "Home",
        icon = ImageVector.vectorResource(id = R.drawable.mosque_icon),
        route = "home"
    ),
    BottomNavigationItem(
        title = "Qibla",
        icon = ImageVector.vectorResource(id = R.drawable.compass_icon),
        route = "qibla"
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
        title = "Settings",
        icon = ImageVector.vectorResource(id = R.drawable.settings_icon),
        route = "contact"
    )
)

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    PrayerTimeTheme() {

    }

}


