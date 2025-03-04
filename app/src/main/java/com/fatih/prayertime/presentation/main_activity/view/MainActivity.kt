package com.fatih.prayertime.presentation.main_activity.view

import android.annotation.SuppressLint
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
import androidx.compose.foundation.layout.PaddingValues


import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size


import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button


import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemColors
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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
import androidx.compose.ui.res.painterResource

import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.fatih.prayertime.domain.model.ThemeOption
import com.fatih.prayertime.domain.use_case.alarm_use_cases.ScheduleDailyAlarmUpdateUseCase
import com.fatih.prayertime.domain.use_case.formatted_use_cases.FormattedUseCase
import com.fatih.prayertime.presentation.calendar_screen.view.CalendarScreen
import com.fatih.prayertime.presentation.compass_screen.view.CompassScreen
import com.fatih.prayertime.presentation.main_activity.viewmodel.AppViewModel
import com.fatih.prayertime.presentation.main_screen.view.MainScreen
import com.fatih.prayertime.presentation.esmaulhusna_screen.view.EsmaulHusnaScreen
import com.fatih.prayertime.presentation.hadith_collections_screen.view.HadithCollectionScreen
import com.fatih.prayertime.presentation.hadith_screen.view.HadithScreen
import com.fatih.prayertime.presentation.settings_screen.view.SettingsScreen
import com.fatih.prayertime.presentation.ui.theme.PrayerTimeTheme
import com.fatih.prayertime.presentation.util_screen.view.UtilitiesScreen
import com.fatih.prayertime.util.Constants.screens
import com.fatih.prayertime.util.ResourceType
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


    @SuppressLint("RestrictedApi")
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
            PrayerTimeTheme(darkTheme = darkTheme) {
                MainScreenContent(::showBatteryOptimizationDialog)
            }
        }
    }
    private fun showBatteryOptimizationDialog() {
        AlertDialog.Builder(this)
            .setTitle("MIUI Battery Optimization")
            .setMessage("In order for notifications to work properly on Xiaomi model phones, you need to cancel the background restrictions. Disable background battery saving from settings. ")
            .setPositiveButton("Settings") { _, _ ->
                val intent = Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS)
                startActivity(intent)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}

@Composable
fun MainScreenContent(showBatteryOptimizationDialog: () -> Unit) {
    val appViewModel: AppViewModel = hiltViewModel()
    val navController = rememberNavController()
    val context = LocalContext.current

    // Local UI state
    var isCalculating by remember { mutableStateOf(true) }

    val powerSavingState by appViewModel.powerSavingState.collectAsState()
    val isLocationPermissionGranted by appViewModel.isLocationPermissionGranted.collectAsState()

    // Permission request launcher
    val permissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            appViewModel.onLocationPermissionResult(permissions, context as ComponentActivity)
            isCalculating = false
        }

    val resultLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            appViewModel.checkLocationPermission()
            appViewModel.checkPowerSavingMode()
        }

    // Request permissions on initial launch
    LaunchedEffect(key1 = Unit) {
        appViewModel.checkLocationPermission()
        appViewModel.checkPowerSavingMode()
        if (!isLocationPermissionGranted) {
            permissionLauncher.launch(appViewModel.locationPermissions)
            isCalculating = true
        }
    }

    // Scaffold Layout
    Scaffold(
        snackbarHost = {
            if (!isLocationPermissionGranted && !isCalculating) {
                Snackbar(
                    action = {
                        Button(onClick = {
                            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                data = Uri.fromParts("package", context.packageName, null)
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
            BottomAppBarLayout(navController)
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(
                    15.dp,
                    top = innerPadding.calculateTopPadding(),
                    15.dp,
                    0.dp
                )
                .background(MaterialTheme.colorScheme.background),
        ) {
            if (powerSavingState == true) {
                showBatteryOptimizationDialog()
            }

            NavHostLayout(navController, innerPadding,appViewModel)
        }
    }
}

@Composable
fun BottomAppBarLayout(navController: NavController) {
    var selectedItemIndex by rememberSaveable { mutableIntStateOf(0) }

    BottomAppBar(
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        modifier = Modifier.clip(RoundedCornerShape(30.dp)),
        tonalElevation = 10.dp,
    ) {
        screens.filterIndexed { index, _ -> index <= 3 }.forEachIndexed { index, item ->
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = ImageVector.vectorResource(id = item.iconRoute),
                        contentDescription = item.title,
                        modifier = Modifier.size(24.dp)
                    )
                },
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
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = false
                            }
                            launchSingleTop = true
                            restoreState = false
                            navController.popBackStack()
                        }
                    }
                }
            )
        }
    }
}

@Composable
fun NavHostLayout(navController: NavHostController, innerPadding: PaddingValues,appViewModel: AppViewModel) {
    val innerPaddingValue = innerPadding.calculateBottomPadding() - 5.dp
    NavHost(
        navController = navController,
        startDestination = screens.first().title
    ) {
        screens.forEach { item ->
            composable(
                route = item.route,
                arguments = item.arguments
            ) { backStackEntry ->
                when (item.title) {
                    "Home" -> MainScreen(appViewModel, innerPaddingValue)
                    "Qibla" -> CompassScreen(innerPaddingValue)
                    "Utilities" -> UtilitiesScreen(innerPaddingValue, navController)
                    "Settings" -> SettingsScreen(innerPaddingValue)
                    "Esmaul Husna" -> EsmaulHusnaScreen(innerPaddingValue)
                    "Islamic Calendar" -> CalendarScreen(innerPaddingValue)
                    "Hadith" -> HadithScreen(innerPaddingValue,navController)
                    "HadithCollections" -> {
                        val collectionPath = backStackEntry.arguments?.getString("collectionPath") ?: return@composable
                        HadithCollectionScreen(innerPadding.calculateBottomPadding() - 5.dp,collectionPath)
                    }
                }
            }
        }
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

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    PrayerTimeTheme(darkTheme = true) {

    }

}


