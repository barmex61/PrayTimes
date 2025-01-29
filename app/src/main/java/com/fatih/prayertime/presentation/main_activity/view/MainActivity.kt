package com.fatih.prayertime.presentation.main_activity.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.with
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Face
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.fatih.prayertime.R
import com.fatih.prayertime.data.gyroscope.GyroscopeSensor
import com.fatih.prayertime.domain.use_case.alarm_use_cases.ScheduleDailyAlarmUpdateUseCase
import com.fatih.prayertime.presentation.compass_screen.view.CompassScreen
import com.fatih.prayertime.presentation.compass_screen.viewmodel.CompassScreenViewModel
import com.fatih.prayertime.presentation.main_activity.viewmodel.AppViewModel
import com.fatih.prayertime.presentation.main_screen.view.MainScreen
import com.fatih.prayertime.presentation.ui.theme.BackGround
import com.fatih.prayertime.presentation.ui.theme.IconColor
import com.fatih.prayertime.presentation.ui.theme.PrayerTimeTheme
import com.fatih.prayertime.util.BottomNavigationItem
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var scheduleDailyAlarmUpdateUseCase: ScheduleDailyAlarmUpdateUseCase
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
                val navController = rememberNavController()
                val bottomNavItems = bottomNavItems()
                val appViewModel: AppViewModel = hiltViewModel()
                val isLocationPermissionGranted by appViewModel.isLocationPermissionGranted.collectAsState()
                var isCalculating by remember { mutableStateOf(true) }
                val showLocationPermissionRationale by appViewModel.showLocationPermissionRationale.collectAsState()
                val permissionLauncher =
                    rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
                        appViewModel.onLocationPermissionResult(permissions, this)
                        isCalculating = false
                    }
                val resultLauncher =
                    rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { appViewModel.checkLocationPermission() }

                LaunchedEffect(key1 = Unit) {
                    appViewModel.checkLocationPermission()
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
                                        navController.navigate(item.title)
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
                                            MainScreen(appViewModel)
                                        }

                                        "Qibla" -> {
                                            CompassScreen()
                                        }

                                        "Profile" -> {
                                            //ProfileScreen()
                                        }

                                        "About" -> {
                                            //AboutScreen()
                                        }

                                        "Contact" -> {
                                            //ContactScreen()
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            val workInfos = WorkManager.getInstance(applicationContext).getWorkInfosByTagLiveData("AlarmWorker")

            workInfos.observe(this) { workInfoList ->
                if (!workInfoList.any { it.state == WorkInfo.State.ENQUEUED || it.state == WorkInfo.State.RUNNING }) {
                    scheduleDailyAlarmUpdateUseCase.execute(this)
                }
                workInfoList.forEach { workInfo ->
                    println("WorkInfo ID: ${workInfo.id}")
                    println("State: ${workInfo.state}")
                    println("Next Schedule Time: ${workInfo.nextScheduleTimeMillis}")
                }
            }
        }
    }

    @Composable
    fun bottomNavItems() = listOf(
        BottomNavigationItem(
            title = "Home",
            icon = Icons.Outlined.Face,
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
            title = "Contact",
            icon = Icons.Outlined.Face,
            route = "contact"
        )
    )
@Composable
fun KaabaRepresentation(offsetX : Dp,offsetY : Dp,color: Color){
    Box(contentAlignment = Alignment.Center) {
        Box(
            modifier = Modifier
                .size(70.dp)
                .border(
                    width = 2.dp,
                    color = color,
                    shape = CircleShape
                )
        )
        Image(
            painter = painterResource(id = R.drawable.kabe),
            contentDescription = "Kaaba",
            modifier = Modifier
                .size(50.dp)
                .offset(offsetX,offsetY)
        )
    }
}
@OptIn(ExperimentalAnimationApi::class)
@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    PrayerTimeTheme(dynamicColor = false, darkTheme = false) {
        val qiblaDirection = 65.0
        var rotation by remember { mutableDoubleStateOf(0.0) }
        LaunchedEffect(key1 = Unit) {
            while (true){
                Log.d("GreetingPreviewLog", "onCreate: $rotation")
                rotation += 1
                delay(10)
            }

        }
        Column(
            modifier = Modifier.fillMaxSize(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            var inRange by remember { mutableStateOf(false) }
            val animatedColor = animateColorAsState(
                targetValue = if (inRange) Color.Green else Color.Red,
                animationSpec = tween(1000),
                label = ""
            )
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(10.dp),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.padding(15.dp)
            ) {

                val infiniteTransition = rememberInfiniteTransition(label = "")
                val animatedRotationValue = infiniteTransition.animateFloat(
                    initialValue = 0f,
                    targetValue = if (inRange) 0f else 360f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(2000),
                        repeatMode = RepeatMode.Restart
                    ),
                    label = "",
                )
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(15.dp)
                ) {
                val icon = if (inRange) R.drawable.check_circle else R.drawable.rotate_arrow_icon
                Spacer(modifier = Modifier.size(10.dp))
                Text(
                    text = "Telefonunuzu kabeyi gösterecek şekilde çevirin",
                    color = animatedColor.value,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.size(20.dp))
                AnimatedContent(
                    targetState = icon,
                    label = "",
                    transitionSpec = {
                        fadeIn(animationSpec = tween(1000)) with
                                fadeOut(animationSpec = tween(1000))
                    }
                ) {
                    Image(
                        painter = painterResource(id = it),
                        contentDescription = "Rotate Indicator",
                        modifier = Modifier
                            .size(50.dp)
                            .rotate(animatedRotationValue.value),
                        colorFilter = ColorFilter.tint(animatedColor.value)
                    )
                }
                Row (
                    modifier = Modifier.fillMaxWidth(1f).padding(top = 30.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ){
                    Image(
                        painter = painterResource(id = R.drawable.cross_icon),
                        contentDescription = "Cross",
                        modifier = Modifier.size(25.dp)
                    )
                    KaabaRepresentation((-25).dp,0.dp,Color.Red)
                    KaabaRepresentation(0.dp,0.dp,Color.Green)
                    Image(
                        painter = painterResource(id = R.drawable.check_circle),
                        contentDescription = "Check",
                        modifier = Modifier.size(25.dp)
                    )

                }
                }
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxSize(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(70.dp)
                            .border(
                                width = 2.dp,
                                color = animatedColor.value,
                                shape = CircleShape
                            )
                    )
                    val totalDegree = qiblaDirection + rotation
                    inRange = totalDegree in -1.5f..1.5f ||
                            totalDegree in 358.5f..361.5f

                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .padding(top = 20.dp)
                            .rotate(-rotation.toFloat())
                    ) {

                        Image(
                            painter = painterResource(id = R.drawable.compass_new),
                            contentDescription = "Compass",
                            modifier = Modifier.size(250.dp),
                        )
                        val compassRadius = 125.dp // Compass radius in px
                        val circleRadius = 35.dp
                        val kaabaRadius = 25.dp// Circle radius in px (70.dp / 2)
                        val distanceBetweenCenters = compassRadius + kaabaRadius + 20.dp + circleRadius /3 // Distance between the centers

                        val angleInRadians = Math.toRadians(qiblaDirection)
                        val xOffset = (distanceBetweenCenters) * kotlin.math.sin(angleInRadians).toFloat()
                        val yOffset = (distanceBetweenCenters * kotlin.math.cos(angleInRadians).toFloat())
                        Text(
                            text = yOffset.toString(),
                            modifier = Modifier.offset(0.dp, (-150).dp)
                        )
                        Image(
                            painter = painterResource(id = R.drawable.kabe),
                            contentDescription = "Kaaba",
                            modifier = Modifier
                                .size(50.dp)
                                .offset(xOffset,-yOffset)
                                .rotate(rotation.toFloat())
                        )
                    }
                }
            }


        }
    }
}
}

