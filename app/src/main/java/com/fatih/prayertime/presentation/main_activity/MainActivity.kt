package com.fatih.prayertime.presentation.main_activity

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

import androidx.compose.ui.unit.dp

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemColors
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.fatih.prayertime.domain.model.ThemeOption
import com.fatih.prayertime.domain.use_case.alarm_use_cases.ScheduleDailyAlarmUpdateUseCase
import com.fatih.prayertime.domain.use_case.formatted_use_cases.FormattedUseCase
import com.fatih.prayertime.presentation.calendar_screen.CalendarScreen
import com.fatih.prayertime.presentation.compass_screen.CompassScreen
import com.fatih.prayertime.presentation.dua_screens.dua_categories_screen.DuaCategoriesScreen
import com.fatih.prayertime.presentation.dua_screens.dua_category_detail_screen.DuaCategoryDetailScreen
import com.fatih.prayertime.presentation.dua_screens.dua_detail_screen.DuaDetailScreen
import com.fatih.prayertime.presentation.dua_screens.dua_categories_screen.DuaCategoriesViewModel
import com.fatih.prayertime.presentation.main_screen.MainScreen
import com.fatih.prayertime.presentation.esmaul_husna_screen.EsmaulHusnaScreen
import com.fatih.prayertime.presentation.favorites_screen.FavoritesScreen
import com.fatih.prayertime.presentation.hadith_screens.HadithCollectionScreen
import com.fatih.prayertime.presentation.hadith_screens.HadithEditionsScreen
import com.fatih.prayertime.presentation.hadith_screens.HadithSectionDetailScreen
import com.fatih.prayertime.presentation.hadith_screens.HadithViewModel
import com.fatih.prayertime.presentation.quran_surah_detail_screen.QuranDetailScreen
import com.fatih.prayertime.presentation.quran_screen.QuranScreen
import com.fatih.prayertime.presentation.settings_screen.PrayerCalculationMethodDialog
import com.fatih.prayertime.presentation.settings_screen.PrayerTimeTuneDialog
import com.fatih.prayertime.presentation.settings_screen.SettingsScreen
import com.fatih.prayertime.presentation.statistics_screen.StatisticsScreen
import com.fatih.prayertime.presentation.ui.theme.PrayerTimeTheme
import com.fatih.prayertime.presentation.util_screen.UtilitiesScreen
import com.fatih.prayertime.util.composables.ErrorView
import com.fatih.prayertime.util.composables.LottieAnimationOnce
import com.fatih.prayertime.util.composables.LottieAnimationOnceRaw
import com.fatih.prayertime.util.config.NavigationConfig.screens
import com.fatih.prayertime.util.model.enums.PrayTimesString
import com.google.accompanist.navigation.animation.AnimatedNavHost
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
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
            val darkTheme = when(settings.selectedTheme){
                ThemeOption.DARK -> true
                ThemeOption.LIGHT -> false
                ThemeOption.SYSTEM_DEFAULT -> isSystemInDarkTheme()
            }
            UpdateSystemBars(darkTheme)
            
            var showSplash by remember { mutableStateOf(true) }
            
            PrayerTimeTheme(darkTheme = darkTheme) {
                if (showSplash) {
                    SplashScreen { showSplash = false }
                } else {
                    MainScreenContent(::showBatteryOptimizationDialog, mainActivityViewModel)
                }
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
fun SplashScreen(onSplashFinished: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        LottieAnimationOnce(
            lottieFile = "splash_screen_islam.lottie",
            modifier = Modifier.fillMaxSize(),
            onFinish = onSplashFinished
        )
        
        LaunchedEffect(Unit) {
            delay(3000)
            onSplashFinished()
        }
    }
}

@Composable
fun MainScreenContent(showBatteryOptimizationDialog: () -> Unit,mainActivityViewModel: MainActivityViewModel) {
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
                showBatteryOptimizationDialog()
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

    }

}


