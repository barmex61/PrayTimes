package com.fatih.prayertime.presentation.main_activity

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

import androidx.compose.ui.unit.dp

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemColors
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.fatih.prayertime.R
import com.fatih.prayertime.data.alarm.LocationUpdateForegroundService
import com.fatih.prayertime.domain.model.ThemeOption
import com.fatih.prayertime.domain.use_case.alarm_use_cases.ScheduleDailyAlarmUpdateUseCase
import com.fatih.prayertime.domain.use_case.formatted_use_cases.FormattedUseCase
import com.fatih.prayertime.presentation.calendar_screen.CalendarScreen
import com.fatih.prayertime.presentation.compass_screen.CompassScreen
import com.fatih.prayertime.presentation.dua_screens.dua_categories_screen.DuaCategoriesScreen
import com.fatih.prayertime.presentation.dua_screens.dua_category_detail_screen.DuaCategoryDetailScreen
import com.fatih.prayertime.presentation.dua_screens.dua_detail_screen.DuaDetailScreen
import com.fatih.prayertime.presentation.main_screen.MainScreen
import com.fatih.prayertime.presentation.esmaul_husna_screen.EsmaulHusnaScreen
import com.fatih.prayertime.presentation.favorites_screen.FavoritesScreen
import com.fatih.prayertime.presentation.hadith_screens.HadithCollectionScreen
import com.fatih.prayertime.presentation.hadith_screens.HadithEditionsScreen
import com.fatih.prayertime.presentation.hadith_screens.HadithSectionDetailScreen
import com.fatih.prayertime.presentation.hadith_screens.HadithViewModel
import com.fatih.prayertime.presentation.quran_surah_detail_screen.QuranDetailScreen
import com.fatih.prayertime.presentation.quran_screen.QuranScreen
import com.fatih.prayertime.presentation.settings_screen.SettingsScreen
import com.fatih.prayertime.presentation.statistics_screen.StatisticsScreen
import com.fatih.prayertime.presentation.ui.theme.PrayerTimeTheme
import com.fatih.prayertime.presentation.util_screen.UtilitiesScreen
import com.fatih.prayertime.util.composables.FullScreenLottieAnimation
import com.fatih.prayertime.util.config.NavigationConfig.screens
import com.fatih.prayertime.util.model.enums.PrayTimesString
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
            val mainActivityViewModel : MainActivityViewModel = hiltViewModel()
            val settings by mainActivityViewModel.settingsState.collectAsState()
            var dontAskAgain by rememberSaveable { mutableStateOf(false) }
            val powerSaveState = mainActivityViewModel.permissionAndPreferences.powerSavingState.collectAsStateWithLifecycle()

            val darkTheme = when(settings.selectedTheme){
                ThemeOption.DARK -> true
                ThemeOption.LIGHT -> false
                ThemeOption.SYSTEM_DEFAULT -> isSystemInDarkTheme()
            }
            UpdateSystemBars(darkTheme)
            FullScreenLottieAnimation(
                lottieFile = "splash_screen_anim.lottie",
                autoPlay = true,
                loop = true,
                speed = 1.75f,
                lottieAnimDuration = 1000
            ) {
                PrayerTimeTheme(darkTheme = darkTheme) {
                    MainActivityContent(mainActivityViewModel){
                        mainActivityViewModel.permissionAndPreferences.checkPowerSavingMode()
                        if (!dontAskAgain && powerSaveState.value == true){
                            ShowBatteryOptimizationDialog()
                        }
                    }
                }
            }

            ScheduleAlarm(scheduleDailyAlarmUpdateUseCase)
        }
    }

    @Composable
    private fun ShowBatteryOptimizationDialog() {
        val dialogMessage = stringResource(R.string.power_saving_dialog)
        val settingsStr = stringResource(R.string.settings)
        val title = stringResource(R.string.power_saving_dialog_title)
        AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(dialogMessage)
            .setPositiveButton(settingsStr) { _, _ ->
                val intent = Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS)
                startActivity(intent)
            }
            .setNegativeButton(stringResource(R.string.cancel), null)
            .show()
    }
}


@Composable
fun MainActivityContent( mainActivityViewModel: MainActivityViewModel, powerSavingDialog : @Composable () -> Unit) {
    val navController = rememberNavController()
    val context = LocalContext.current

    val powerSavingState by mainActivityViewModel.permissionAndPreferences.powerSavingState.collectAsStateWithLifecycle()
    val isLocationPermissionGranted by mainActivityViewModel.permissionAndPreferences.isLocationPermissionGranted.collectAsStateWithLifecycle()
    val isNotificationPermissionGranted by mainActivityViewModel.permissionAndPreferences.isNotificationPermissionGranted.collectAsStateWithLifecycle()
    val isAlarmPermissionGranted by mainActivityViewModel.permissionAndPreferences.isAlarmPermissionGranted.collectAsStateWithLifecycle()

    val permissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            mainActivityViewModel.permissionAndPreferences.onPermissionResult(permissions)
        }

    val resultLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            mainActivityViewModel.permissionAndPreferences.checkLocationPermission()
            mainActivityViewModel.permissionAndPreferences.checkPowerSavingMode()
            mainActivityViewModel.permissionAndPreferences.checkNotificationPermission()
            mainActivityViewModel.permissionAndPreferences.checkAlarmPermission()
        }

    LaunchedEffect(key1 = Unit) {
        mainActivityViewModel.permissionAndPreferences.checkLocationPermission()
        mainActivityViewModel.permissionAndPreferences.checkPowerSavingMode()
        mainActivityViewModel.permissionAndPreferences.checkNotificationPermission()
        mainActivityViewModel.permissionAndPreferences.checkAlarmPermission()
        
        if (!isLocationPermissionGranted || !isNotificationPermissionGranted || !isAlarmPermissionGranted) {
            val permissionList = mutableListOf<String>()
            
            permissionList.addAll(mainActivityViewModel.permissionAndPreferences.locationPermissions)
            
            mainActivityViewModel.permissionAndPreferences.notificationPermission?.let {
                permissionList.add(it)
            }
            
            mainActivityViewModel.permissionAndPreferences.alarmPermission?.let {
                permissionList.add(it)
            }
            
            permissionLauncher.launch(permissionList.toTypedArray())
        }
    }

    Scaffold(
        snackbarHost = {
            if (!isLocationPermissionGranted || !isNotificationPermissionGranted || !isAlarmPermissionGranted) {
                Snackbar(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
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
            modifier = Modifier.background(MaterialTheme.colorScheme.background) ,

        ) {
            if (powerSavingState == true) {
                powerSavingDialog()
            }

            NavHostLayout(navController, innerPadding)
        }
    }
}

@Composable
fun BottomAppBarLayout(navController: NavController) {
    var selectedItemIndex by rememberSaveable { mutableIntStateOf(0) }

    BottomAppBar(
        containerColor = MaterialTheme.colorScheme.primaryContainer,
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
fun NavHostLayout(navController: NavHostController, innerPadding: PaddingValues) {
    val modifier = Modifier.padding(
        15.dp,
        innerPadding.calculateTopPadding(),
        15.dp,
        innerPadding.calculateBottomPadding()
    )
    val hadithViewModel : HadithViewModel = hiltViewModel()
    AnimatedNavHost(
        navController = navController,
        startDestination = screens.first().title.name
    ) {
        screens.forEachIndexed { index , item ->
            composable(
                route = item.route,
                enterTransition = {
                    fadeIn(tween(500)) + scaleIn(tween(500))
                },
                exitTransition = {
                    fadeOut(tween(500)) + scaleOut(tween(500))
                },
                popEnterTransition = {
                    fadeIn(tween(500)) + scaleIn(tween(500))
                },
                popExitTransition = {
                    fadeOut(tween(500)) + scaleOut(tween(500))
                }
            ) { backStackEntry ->

                when (item.title.name) {
                    PrayTimesString.Home.name -> MainScreen(modifier,navController)
                    PrayTimesString.Qibla.name -> CompassScreen(modifier)
                    PrayTimesString.Utilities.name -> UtilitiesScreen(modifier, navController)
                    PrayTimesString.Settings.name -> SettingsScreen(modifier)
                    PrayTimesString.ESMAUL_HUSNA.name -> EsmaulHusnaScreen(modifier)
                    PrayTimesString.ISLAMIC_CALENDAR.name -> CalendarScreen(modifier)
                    PrayTimesString.HADITH.name-> HadithEditionsScreen(modifier,navController)
                    PrayTimesString.HADITH_COLLECTION.name -> {
                        val collectionPath = backStackEntry.arguments?.getString("collectionPath") ?: return@composable
                        HadithCollectionScreen(modifier,collectionPath, hadithViewModel = hadithViewModel, navController = navController)
                    }
                    PrayTimesString.HADITH_SECTION_DETAILS.name -> {
                        val collectionPath = backStackEntry.arguments?.getString("collectionPath")
                        val hadithSectionIndex = backStackEntry.arguments?.getString("hadithSectionIndex")
                        val hadithIndex = backStackEntry.arguments?.getString("hadithIndex")
                        HadithSectionDetailScreen(modifier, hadithViewModel, hadithSectionIndex?.toIntOrNull(), collectionPath,hadithIndex?.toIntOrNull())
                    }
                    PrayTimesString.PRAYER.name -> DuaCategoriesScreen(modifier,navController)
                    PrayTimesString.PRAY_CATEGORY_DETAILS.name -> {
                        val categoryId = backStackEntry.arguments?.getString("categoryId") ?: return@composable
                        DuaCategoryDetailScreen(modifier,navController,categoryId.toInt())
                    }

                    PrayTimesString.PRAYER_DETAIL.name -> {
                        val duaId = backStackEntry.arguments?.getString("duaId") ?: return@composable
                        val categoryId = backStackEntry.arguments?.getString("categoryId") ?: return@composable
                        DuaDetailScreen(modifier,duaId.toInt(),categoryId.toInt())
                    }
                    PrayTimesString.FAVORITES.name -> FavoritesScreen(modifier,navController)
                    PrayTimesString.STATISTICS.name -> StatisticsScreen(modifier)
                    PrayTimesString.QURAN.name -> { QuranScreen(modifier,navController) }
                    PrayTimesString.QURAN_DETAIL_SCREEN.name -> {
                        val surahNumber = backStackEntry.arguments?.getString("surahNumber")?:return@composable
                        QuranDetailScreen(surahNumber.toInt(),innerPadding.calculateBottomPadding(),innerPadding.calculateTopPadding())
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

@Composable
fun ScheduleAlarm(scheduleDailyAlarmUpdateUseCase: ScheduleDailyAlarmUpdateUseCase) {
    val context = LocalContext.current
    
    scheduleDailyAlarmUpdateUseCase.executePrayAlarmWorker(context)
    scheduleDailyAlarmUpdateUseCase.executeStatisticsAlarmWorker(context)

    /* DEBUGGG
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomEnd) {
        Button(
            onClick = {
                Toast.makeText(context, context.getString(R.string.statistics_alarms_reset), Toast.LENGTH_SHORT).show()
            },
            modifier = Modifier
                .padding(16.dp)
                .size(56.dp),
            shape = RoundedCornerShape(28.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)
        ) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = stringResource(R.string.reset_alarms),
                tint = MaterialTheme.colorScheme.onTertiary
            )
        }
    }  */
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    PrayerTimeTheme(darkTheme = false) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            Card(
                modifier = Modifier
                    .padding(12.dp)
                    .fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                elevation = CardDefaults.cardElevation(10.dp),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(
                    modifier = Modifier.padding(12.dp),
                    text = stringResource(R.string.settings),
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }

}


