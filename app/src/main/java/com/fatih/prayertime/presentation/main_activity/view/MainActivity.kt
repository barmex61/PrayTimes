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
import com.fatih.prayertime.presentation.main_activity.viewmodel.AppViewModel
import com.fatih.prayertime.presentation.main_screen.view.MainScreen
import com.fatih.prayertime.presentation.ui.theme.BackGround
import com.fatih.prayertime.presentation.ui.theme.IconColor
import com.fatih.prayertime.presentation.ui.theme.PrayerTimeTheme
import com.fatih.prayertime.util.Constants.bottomNavItems
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
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
                val context = LocalContext.current
                val appViewModel : AppViewModel = hiltViewModel()
                val showGoToSettings by  appViewModel.showGoToSettings.collectAsState()
                val showPermissionRequest by appViewModel.showPermissionRequest.collectAsState()
                val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions -> appViewModel.onPermissionsResult(permissions,context as ComponentActivity) }
                val resultLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { appViewModel.checkPermissions(context) }
                if (showPermissionRequest) {
                    LaunchedEffect (Unit){
                        appViewModel.checkPermissions(context)
                        permissionLauncher.launch(appViewModel.locationPermissions)
                    }
                }
                Scaffold(
                    snackbarHost = {
                        if (showPermissionRequest) {
                            Snackbar(
                                action = {
                                    Button(onClick = {
                                        if (showGoToSettings){
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
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    PrayerTimeTheme(dynamicColor = false, darkTheme = false) {
    

    }

}


