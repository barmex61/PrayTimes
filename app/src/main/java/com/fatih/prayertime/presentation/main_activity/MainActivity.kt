package com.fatih.prayertime.presentation.main_activity

import DuaCategoryDetailScreen
import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically


import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
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
import androidx.compose.ui.res.stringResource

import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.fatih.prayertime.data.remote.dto.duadto.DuaCategoryDetail
import com.fatih.prayertime.domain.model.ThemeOption
import com.fatih.prayertime.domain.use_case.alarm_use_cases.ScheduleDailyAlarmUpdateUseCase
import com.fatih.prayertime.domain.use_case.formatted_use_cases.FormattedUseCase
import com.fatih.prayertime.presentation.calendar_screen.CalendarScreen
import com.fatih.prayertime.presentation.compass_screen.CompassScreen
import com.fatih.prayertime.presentation.dua_categories_screen.DuaCategoriesScreen
import com.fatih.prayertime.presentation.dua_categories_screen.DuaCategoriesViewModel
import com.fatih.prayertime.presentation.dua_detail_screen.DuaDetailScreen
import com.fatih.prayertime.presentation.main_screen.MainScreen
import com.fatih.prayertime.presentation.esmaulhusna_screen.EsmaulHusnaScreen
import com.fatih.prayertime.presentation.hadith_collections_screen.HadithCollectionScreen
import com.fatih.prayertime.presentation.hadith_collections_screen.HadithCollectionViewModel
import com.fatih.prayertime.presentation.hadith_section_detail_screen.HadithSectionDetailScreen
import com.fatih.prayertime.presentation.hadith_screen.HadithScreen
import com.fatih.prayertime.presentation.settings_screen.SettingsScreen
import com.fatih.prayertime.presentation.ui.theme.PrayerTimeTheme
import com.fatih.prayertime.presentation.util_screen.UtilitiesScreen
import com.fatih.prayertime.util.Constants.screens
import com.fatih.prayertime.util.PrayTimesString
import com.google.accompanist.navigation.animation.AnimatedNavHost
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
            ScheduleAlarm(scheduleDailyAlarmUpdateUseCase)
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
                        contentDescription = item.title.name,
                        modifier = Modifier.size(24.dp)
                    )
                },
                label = { Text(text = stringResource(item.title.stringResId)) },
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

                    if (currentScreen != item.title.name) {
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

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun NavHostLayout(navController: NavHostController, innerPadding: PaddingValues,appViewModel: AppViewModel) {
    val innerPaddingValue = innerPadding.calculateBottomPadding() - 5.dp
    val hadithCollectionViewModel : HadithCollectionViewModel = hiltViewModel()
    val duaCategoriesViewModel : DuaCategoriesViewModel = hiltViewModel()
    AnimatedNavHost(
        navController = navController,
        startDestination = screens.first().title.name
    ) {
        screens.forEachIndexed { index , item ->
            composable(
                route = item.route,
                arguments = item.arguments,
                enterTransition = {
                    getEnterTransition(index)
                },
                exitTransition = {
                    getExitTransition(index)
                },
                popEnterTransition = {
                    getEnterTransition(index)
                },
                popExitTransition = {
                    getExitTransition(index)
                }
            ) { backStackEntry ->

                when (item.title.name) {
                    PrayTimesString.Home.name -> MainScreen(appViewModel, innerPaddingValue)
                    PrayTimesString.Qibla.name -> CompassScreen(innerPaddingValue)
                    PrayTimesString.Utilities.name -> UtilitiesScreen(innerPaddingValue, navController)
                    PrayTimesString.Settings.name -> SettingsScreen(innerPaddingValue)
                    PrayTimesString.ESMAUL_HUSNA.name -> EsmaulHusnaScreen(innerPaddingValue)
                    PrayTimesString.ISLAMIC_CALENDAR.name -> CalendarScreen(innerPaddingValue)
                    PrayTimesString.HADITH.name-> HadithScreen(innerPaddingValue,navController)
                    PrayTimesString.HADITH_COLLECTION.name -> {
                        val collectionPath = backStackEntry.arguments?.getString("collectionPath") ?: return@composable
                        HadithCollectionScreen(innerPaddingValue,collectionPath, hadithCollectionViewModel = hadithCollectionViewModel, navController = navController)
                    }
                    PrayTimesString.HADITH_SECTION_DETAILS.name -> HadithSectionDetailScreen(innerPaddingValue,hadithCollectionViewModel)
                    PrayTimesString.PRAYER.name -> DuaCategoriesScreen(innerPaddingValue,navController,duaCategoriesViewModel)
                    PrayTimesString.PRAY_CATEGORY_DETAILS.name -> DuaCategoryDetailScreen(innerPaddingValue,duaCategoriesViewModel,navController)
                    PrayTimesString.PRAYER_DETAIL.name -> DuaDetailScreen(innerPaddingValue,duaCategoriesViewModel)
                }
            }
        }
    }
}

private fun getEnterTransition(index : Int) : EnterTransition{
   return if (index <= 2){
        slideInVertically(animationSpec = tween(1000)){height -> -height} + fadeIn(animationSpec = tween(700))
    }else{
        slideInHorizontally( animationSpec = tween(1000)){width -> width} + fadeIn(animationSpec = tween(700))
    }
}
private fun getExitTransition(index: Int) : ExitTransition{
    return if (index <= 2){
        slideOutVertically(animationSpec = tween(1000)){height -> height} + fadeOut(animationSpec = tween(700))
    }else{
        slideOutHorizontally( animationSpec = tween(1000)){width -> -width} + fadeOut(animationSpec = tween(700))
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
fun ScheduleAlarm(scheduleDailyAlarmUpdateUseCase: ScheduleDailyAlarmUpdateUseCase) {
    val context = LocalContext.current
    scheduleDailyAlarmUpdateUseCase.execute(context)
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    PrayerTimeTheme(darkTheme = true) {

    }

}


