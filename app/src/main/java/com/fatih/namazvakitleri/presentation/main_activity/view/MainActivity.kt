package com.fatih.namazvakitleri.presentation.main_activity.view

import android.annotation.SuppressLint
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import com.google.android.gms.location.LocationRequest
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemColors
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.fatih.namazvakitleri.presentation.main_activity.viewmodel.MainActivityViewModel
import com.fatih.namazvakitleri.presentation.main_screen.view.DailyPrayCompose
import com.fatih.namazvakitleri.presentation.main_screen.view.MainScreen
import com.fatih.namazvakitleri.presentation.main_screen.view.PrayNotificationCompose
import com.fatih.namazvakitleri.presentation.main_screen.view.PrayScheduleCompose
import com.fatih.namazvakitleri.presentation.main_screen.view.TopBarCompose
import com.fatih.namazvakitleri.presentation.ui.theme.BackGround
import com.fatih.namazvakitleri.presentation.ui.theme.IconColor
import com.fatih.namazvakitleri.presentation.ui.theme.NamazVakitleriTheme
import com.fatih.namazvakitleri.util.Constants.bottomNavItems
import com.fatih.namazvakitleri.util.Status
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
            NamazVakitleriTheme(dynamicColor = false, darkTheme = false) {
                val viewModel : MainActivityViewModel = hiltViewModel()
                val context = LocalContext.current

                val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions -> viewModel.onPermissionsResult(permissions,this) }
                val resultLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { viewModel.checkPermissions(context) }

                val showGoToSettings by viewModel.showGoToSettings.collectAsState()
                val showPermissionRequest by viewModel.showPermissionRequest.collectAsState()

                if (showPermissionRequest) {
                    LaunchedEffect (Unit){
                        permissionLauncher.launch(viewModel.locationPermissions)
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
                                            permissionLauncher.launch(viewModel.locationPermissions)
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
                        MainScreen()
                        GetLocationInformation(viewModel)
                    }
                }
            }
        }
    }
}

@SuppressLint("MissingPermission")
@Composable
fun GetLocationInformation(viewModel: MainActivityViewModel){
    val permissionGranted by viewModel.permissionGranted.collectAsState()
    LaunchedEffect (key1 = permissionGranted) {
        (1..5).forEach {
            viewModel.getLocationAndAddress()
        }
    }
    val currentAddress by viewModel.locationAndAddress.collectAsState()
    val country = currentAddress.data?.country
    val city = currentAddress.data?.city
    val district  = currentAddress.data?.street
    val street = currentAddress.data?.district
    val fullAddress = currentAddress.data?.fullAddress
    println("heyos")
    if (currentAddress.status == Status.SUCCESS){
        Column {
            Text(text = "Location: ${currentAddress.data?.latitude}, ${currentAddress.data?.longitude}")
            Text(text = "Country: $country")
            Text(text = "City: $city")
            Text(text = "District: $district")
            Text(text = "Street: $street")
            Text(text = "Full Address: $fullAddress")
        }
    } else {
        Text(text = "Location not available")
    }
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    NamazVakitleriTheme(dynamicColor = false, darkTheme = false) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            bottomBar = {
                BottomAppBar(

                ) {
                    var selectedItemIndex by remember { mutableIntStateOf(0) }
                    bottomNavItems.forEachIndexed { index, item ->
                        NavigationBarItem(
                            icon = { Icon(item.icon, contentDescription = item.title) },
                            label = { Text(item.title) },
                            colors = NavigationBarItemColors(
                                selectedIconColor = IconColor,
                                selectedTextColor = IconColor,
                                unselectedIconColor = Color.Black,
                                unselectedTextColor = Color.Black,
                                selectedIndicatorColor = Color.Transparent,
                                disabledIconColor = Color.Gray,
                                disabledTextColor = Color.Gray,

                            ),
                            selected = selectedItemIndex == index,
                            onClick = {
                                selectedItemIndex = index
                                // Burada navigasyon işlemlerini gerçekleştirebilirsiniz.
                                println("Navigating to ${item.route}")
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

                ) {
                val scrollState = rememberScrollState()
                Column(modifier = Modifier.verticalScroll(scrollState, enabled = true) ){
                    TopBarCompose()
                    PrayScheduleCompose()
                    PrayNotificationCompose()
                    DailyPrayCompose()
                }
            }
        }
    }
}


