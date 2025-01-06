package com.fatih.namazvakitleri.presentation.ui

import android.annotation.SuppressLint
import android.location.Location
import android.os.Build
import android.os.Bundle
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
import androidx.compose.material3.AlertDialog
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.fatih.namazvakitleri.presentation.main_screen.view.DailyPrayCompose
import com.fatih.namazvakitleri.presentation.main_screen.view.MainScreen
import com.fatih.namazvakitleri.presentation.main_screen.view.PrayNotificationCompose
import com.fatih.namazvakitleri.presentation.main_screen.view.PrayScheduleCompose
import com.fatih.namazvakitleri.presentation.main_screen.view.TopBarCompose
import com.fatih.namazvakitleri.presentation.ui.theme.BackGround
import com.fatih.namazvakitleri.presentation.ui.theme.IconColor
import com.fatih.namazvakitleri.presentation.ui.theme.NamazVakitleriTheme
import com.fatih.namazvakitleri.util.Constants.bottomNavItems
import com.google.android.gms.location.LocationServices
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
                Scaffold(
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer {

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
                        MainScreen()
                        PermissionControl()
                    }
                }
            }
        }
    }
}

@Composable
fun PermissionControl() {
    val context = LocalContext.current
    var permissionGranted by remember { mutableStateOf(false) }
    var showSnackbar by remember { mutableStateOf(true) }
    val locationPermissions = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
        arrayOf(
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_BACKGROUND_LOCATION
        )
    }else{
        arrayOf(
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        )
    }
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions.all { it.value }) {
            permissionGranted = true
            println("permissionlauncher granted")
        }else{
            showSnackbar = true
            println("permissionlauncher not granted")
        }
    }
    println("before launch effect")

        LaunchedEffect(Unit) {
            println("launcheffect")
            val isAllPermissionsGranted = locationPermissions.all {
                context.checkSelfPermission(it) == android.content.pm.PackageManager.PERMISSION_GRANTED
            }
            if (isAllPermissionsGranted){
                permissionGranted = true
            }
        }


    if (showSnackbar){
        Snackbar (
            action = {
                Button(onClick = {
                    println("onclick")
                    permissionLauncher.launch(locationPermissions)
                }) {
                    Text("Retry")
                }
            }
        ){
            Text("Location permission is required for this app to work")
        }
    }
    if (permissionGranted){
        GetLocationInformation()
    }
}

@SuppressLint("MissingPermission")
@Composable
fun GetLocationInformation(){
    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    var location by remember { mutableStateOf<Location?>(null) }
    LaunchedEffect(Unit) {
        fusedLocationClient.lastLocation.addOnSuccessListener { location = it }
    }
    if (location != null) {
        Text(text = "Location: ${location?.latitude}, ${location?.longitude}")
    }else{
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


