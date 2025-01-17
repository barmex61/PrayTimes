package com.fatih.prayertime.presentation.main_activity.view

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
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemColors
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.fatih.prayertime.domain.use_case.alarm_use_cases.schedule_daily_alarm_update_use_case.ScheduleDailyAlarmUpdateUseCase
import com.fatih.prayertime.presentation.main_activity.viewmodel.AppViewModel
import com.fatih.prayertime.presentation.main_screen.view.MainScreen
import com.fatih.prayertime.presentation.ui.theme.BackGround
import com.fatih.prayertime.presentation.ui.theme.IconColor
import com.fatih.prayertime.presentation.ui.theme.PrayerTimeTheme
import com.fatih.prayertime.util.Constants.bottomNavItems
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var scheduleDailyAlarmUpdateUseCase : ScheduleDailyAlarmUpdateUseCase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            navigationBarStyle = SystemBarStyle.light(
                android.graphics.Color.TRANSPARENT,
                android.graphics.Color.TRANSPARENT
            ),
            statusBarStyle = SystemBarStyle.light(
                android.graphics.Color.TRANSPARENT,
                android.graphics.Color.TRANSPARENT
            )
        )
        setContent {
            PrayerTimeTheme(dynamicColor = false, darkTheme = false) {
                val appViewModel : AppViewModel = hiltViewModel()
                val isLocationPermissionGranted by appViewModel.isLocationPermissionGranted.collectAsState()
                val showLocationPermissionRationale by appViewModel.showLocationPermissionRationale.collectAsState()
                val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions -> appViewModel.onLocationPermissionResult(permissions,this) }
                val resultLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { appViewModel.checkLocationPermission() }

                LaunchedEffect (key1 = Unit){
                    appViewModel.checkLocationPermission()
                    if (!isLocationPermissionGranted){
                        permissionLauncher.launch(appViewModel.locationPermissions)
                    }
                }

                Scaffold(
                    snackbarHost = {
                        if (!isLocationPermissionGranted) {
                            Snackbar(
                                action = {
                                    Button(onClick = {
                                        if (showLocationPermissionRationale){
                                            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                                data = Uri.fromParts("package", packageName, null)
                                            }
                                            resultLauncher.launch(intent)

                                        }else{
                                            permissionLauncher.launch(appViewModel.locationPermissions)
                                        }
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
                            modifier = Modifier.clip(RoundedCornerShape(20.dp)),
                            tonalElevation = 10.dp,
                            containerColor = Color.White
                        ) {
                            var selectedItemIndex by remember { mutableIntStateOf(0) }
                            bottomNavItems.forEachIndexed { index, item ->
                                NavigationBarItem(
                                    icon = { Icon(item.icon, contentDescription = item.title) },
                                    label = { Text(item.title) },
                                    selected = selectedItemIndex == index,
                                    colors = NavigationBarItemColors(
                                        selectedIconColor = IconColor,
                                        selectedTextColor = IconColor,
                                        unselectedIconColor = Color.Black,
                                        unselectedTextColor = Color.Black,
                                        selectedIndicatorColor = Color.Transparent,
                                        disabledIconColor = Color.Gray,
                                        disabledTextColor = Color.Gray,

                                        ),
                                    onClick = {
                                        selectedItemIndex = index
                                    }
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .background(BackGround)
                            .padding(
                                15.dp,
                                innerPadding.calculateTopPadding() + 10.dp,
                                15.dp,
                                innerPadding.calculateBottomPadding()
                            ),
                        )
                    {

                        MainScreen(appViewModel)
                    }
                }
            }
        }
        scheduleDailyAlarmUpdateUseCase.execute(this)
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    PrayerTimeTheme(dynamicColor = false, darkTheme = false) {

    }
}


