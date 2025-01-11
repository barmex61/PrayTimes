package com.fatih.prayertime.presentation.main_screen.view

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.EaseInOutQuad
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandIn
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.with
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.outlined.Face
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.exyte.animatednavbar.utils.noRippleClickable
import com.fatih.prayertime.R
import com.fatih.prayertime.domain.model.GlobalAlarm
import com.fatih.prayertime.domain.model.PrayTimes
import com.fatih.prayertime.presentation.main_activity.view.MainActivity
import com.fatih.prayertime.presentation.main_activity.viewmodel.AppViewModel
import com.fatih.prayertime.presentation.main_screen.viewmodel.MainScreenViewModel
import com.fatih.prayertime.presentation.ui.theme.IconBackGroundColor
import com.fatih.prayertime.presentation.ui.theme.IconColor
import com.fatih.prayertime.presentation.ui.theme.LightGreen
import com.fatih.prayertime.util.NetworkState
import com.fatih.prayertime.util.Status
import com.fatih.prayertime.util.convertTimeToSeconds
import com.fatih.prayertime.util.toAddress
import com.fatih.prayertime.util.toList
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import org.threeten.bp.LocalDate

import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun MainScreen(appViewModel: AppViewModel) {
    val mainScreenViewModel : MainScreenViewModel = hiltViewModel()
    val scrollState = rememberScrollState()
    GetLocationInformation(mainScreenViewModel,appViewModel)
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit){
        isVisible = true
    }
    Column(modifier = Modifier.verticalScroll(scrollState, enabled = true) ){
        AnimatedVisibility(
            visible = isVisible,
            enter = slideInVertically(animationSpec = tween(1000)) + fadeIn(),
            exit = slideOutVertically(animationSpec = tween(1000)) + fadeOut()
        ){
            AddressBar()
        }
        AnimatedVisibility(
            visible = isVisible,
            enter = slideInVertically(animationSpec = tween(1000)) + fadeIn(),
            exit = slideOutVertically(animationSpec = tween(1000)) + fadeOut()
        ){
            PrayerBar()
        }
        AnimatedVisibility(
            visible = isVisible,
            enter = slideInHorizontally(tween(1000)) + fadeIn(),
            exit = slideOutHorizontally(tween(1000)) + fadeOut()
        ){
            PrayScheduleCompose()
        }
        AnimatedVisibility(
            visible = isVisible,
            enter = slideInHorizontally(tween(1000)) + fadeIn(),
            exit = slideOutHorizontally(tween(1000)) + fadeOut()
        ){
            PrayNotificationCompose(mainScreenViewModel,appViewModel)
        }
        AnimatedVisibility(
            visible = isVisible,
            enter = expandIn(expandFrom = Alignment.BottomCenter) + fadeIn(tween(1000)),
            exit = shrinkOut(shrinkTowards = Alignment.TopCenter) + fadeOut(tween(1000))
        ){
            DailyPrayCompose()
        }

    }

}

@Composable
fun GetLocationInformation(mainScreenViewModel: MainScreenViewModel, appViewModel: AppViewModel){
    val permissionGranted by appViewModel.permissionGranted.collectAsState()
    var isLocationTracking by rememberSaveable { mutableStateOf(false) }
    val networkState by appViewModel.networkState.collectAsState()
    LaunchedEffect (key1 = Unit, key2 = permissionGranted) {
        snapshotFlow { networkState }
            .collectLatest { networkState ->
                when(networkState){
                    NetworkState.Connected -> {
                       if (permissionGranted && !isLocationTracking){
                           mainScreenViewModel.trackLocationAndUpdatePrayTimesDatabase()
                           isLocationTracking = true
                       }
                        if (!permissionGranted) {
                            mainScreenViewModel.getDailyPrayTimesFromDb()
                        }
                        if (permissionGranted){
                            mainScreenViewModel.getDailyPrayTimesFromAPI(null)
                        }
                    }
                    NetworkState.Disconnected -> {
                       mainScreenViewModel.getDailyPrayTimesFromDb()
                    }
                }
            }

    }
}


@Composable
fun DailyPrayCompose() {
    Column(modifier = Modifier.padding(top = 10.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Daily Prayer",
                style = MaterialTheme.typography.titleMedium,
                color = LocalContentColor.current.copy(alpha = 0.87f),
                maxLines = 1,
                softWrap = false,
            )
            Spacer(Modifier.weight(1f))
            Card(
                onClick = {},
                colors = CardDefaults.cardColors(containerColor = IconColor),
                elevation = CardDefaults.cardElevation(10.dp),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(
                    modifier = Modifier.padding(4.dp),
                    text = "See All",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.White,
                    maxLines = 1,
                    softWrap = false,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.W600,
                )
            }
        }
        (1..2).forEach { i ->
            Row {
                (1..3).forEach { j ->
                    Card (
                        modifier = Modifier
                            .padding(7.dp)
                            .weight(1f),
                        onClick = {},
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(10.dp),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(top = 10.dp, bottom = 10.dp)
                        ) {
                            Icon(
                                modifier = Modifier.padding(start = 7.dp),
                                imageVector = Icons.Outlined.Face,
                                contentDescription = "Face Icon",
                            )
                            val list = listOf(
                                "Prayer for eating",
                                "Study prayer",
                                "Prayer for sleeping",
                                "Prayer for exam",
                                "Prayer for work",
                                "Prayer for study"
                            )
                            Text(
                                modifier = Modifier
                                    .padding(vertical = 7.dp, horizontal = 7.dp)
                                    .basicMarquee(),
                                text = list[if (i == 1) j-1 else j+2],
                                style = MaterialTheme.typography.bodySmall,
                                maxLines = 1,
                                softWrap = false,
                                textAlign = TextAlign.Center,
                                fontWeight = FontWeight.W600
                            )
                        }
                    }
                }
            }
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrayNotificationCompose(mainScreenViewModel: MainScreenViewModel,appViewModel: AppViewModel) {
    var rotate by remember { mutableStateOf(false) }
    val rotateX = animateFloatAsState(
        targetValue = if (rotate) 180f else 360f,
        animationSpec = tween(1000),
        label = ""
    )
    val alarmRotate = animateFloatAsState(
        targetValue = if (rotate) 360f * 6f else 0f,
        animationSpec = tween(1000), label = ""
    )

    Card(
        modifier = Modifier
            .padding(top = 20.dp)
            .graphicsLayer {
                rotationX = rotateX.value
            },
        onClick = {
            rotate = !rotate
        },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(10.dp),
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            Row(
                modifier = Modifier
                    .padding(10.dp)
                    .fillMaxWidth(1f)
                    .graphicsLayer {
                        rotationX = rotateX.value
                    },
                verticalAlignment = Alignment.CenterVertically
            )
            {
                Icon(
                    modifier = Modifier.graphicsLayer {
                        rotationY = alarmRotate.value
                    },
                    painter = painterResource(R.drawable.alarm_icon),
                    contentDescription = "Alarm Icon"
                )
                Text(
                    modifier = Modifier.padding(start = 5.dp),
                    text = "Prayer Tracker",
                    style = MaterialTheme.typography.titleMedium,
                    color = LocalContentColor.current.copy(alpha = 0.87f),
                    maxLines = 1,
                    softWrap = false,
                    textAlign = TextAlign.Center,
                )
                Spacer(modifier = Modifier.weight(1f))

                val activity = LocalContext.current as ComponentActivity
                val notificationPermissionState by appViewModel.notificationPermissionState.collectAsState()

                val permissionLauncher = rememberLauncherForActivityResult(
                    ActivityResultContracts.RequestPermission()
                ) { isGranted ->
                    println(activity.shouldShowRequestPermissionRationale(android.Manifest.permission.POST_NOTIFICATIONS))
                    appViewModel.onNotificationPermissionResult()
                }
                val settingsLauncher = rememberLauncherForActivityResult(
                    ActivityResultContracts.StartActivityForResult()
                ) {

                }
                Icon(
                    modifier = Modifier.clickable {
                        when {
                            notificationPermissionState.isGranted -> Unit
                            notificationPermissionState.showRationale -> {
                                println("rationale")
                                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                    data = Uri.fromParts("package", activity.packageName, null)
                                }
                                settingsLauncher.launch(intent)
                            }
                            else -> {
                                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                            }
                        }
                    },
                    painter = painterResource(R.drawable.save_icon),
                    contentDescription = "Save Icon"
                )
            }
            Spacer(modifier = Modifier.height(5.dp))
            Card(
                modifier = Modifier
                    .padding(horizontal = 10.dp)
                    .fillMaxWidth(1f)
                    .graphicsLayer {
                        rotationX = rotateX.value
                    },
                onClick = {},
                colors = CardDefaults.cardColors(containerColor = IconBackGroundColor),
                elevation = CardDefaults.cardElevation(5.dp),
                shape = RoundedCornerShape(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    val globalAlarmList by mainScreenViewModel.globalAlarmList.collectAsState()
                    if (globalAlarmList != null) {
                        globalAlarmList!!.forEach { globalAlarm ->
                            LazyColumn(
                                modifier = Modifier
                                    .weight(1f)
                                    .size(70.dp)
                                    .padding(vertical = 10.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable {
                                        println("granted all")
                                        mainScreenViewModel.updateGlobalAlarm(
                                            globalAlarm.alarmType,
                                            System.currentTimeMillis() + 120000L,
                                            !globalAlarm.isEnabled,
                                            15
                                        )

                                    },
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                item(key = globalAlarm.alarmType) {
                                    AlarmComposable(globalAlarm)
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Card(
                modifier = Modifier

                    .background(Color.Transparent)
                    .fillMaxWidth(1f)
                    .padding(start = 10.dp, end = 10.dp, bottom = 15.dp)
                    .graphicsLayer {
                        rotationX = rotateX.value
                    },
                onClick = {},
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(5.dp),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(
                    modifier = Modifier
                        .padding(5.dp)
                        .fillMaxWidth(1f),
                    text = "Prayer Together",
                    style = MaterialTheme.typography.titleLarge,
                    fontSize = 18.sp,
                    color = IconColor,
                    maxLines = 1,
                    softWrap = false,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.W500
                )
            }
        }
    }
}


@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AlarmComposable(globalAlarm: GlobalAlarm) {
    val isChecked = rememberSaveable(globalAlarm.isEnabled) { globalAlarm.isEnabled }
    val iconDrawable = if (isChecked) painterResource(R.drawable.check_circle) else painterResource(R.drawable.cross_icon)

    AnimatedContent(
        targetState = iconDrawable,
        transitionSpec ={
            scaleIn(tween(1000)) + fadeIn(tween(500)) with
                    scaleOut(tween(1000))+ fadeOut(tween(500))
        },
        label = ""

    ) {
        Icon(
            modifier = Modifier
                .padding(top = 3.dp)
               ,
            tint = IconColor,
            painter = it,
            contentDescription = "Check Circle",
        )
    }

    Text(
        text = globalAlarm.alarmType,
        style = MaterialTheme.typography.titleSmall,
        color = LocalContentColor.current.copy(alpha = 0.87f),
        maxLines = 1,
        softWrap = false,
        textAlign = TextAlign.Center,
        fontWeight = FontWeight.W600
    )
}

@SuppressLint("NewApi")
@Composable
fun PrayScheduleCompose() {
    val mainScreenViewModel : MainScreenViewModel = hiltViewModel()
    Card(
        modifier = Modifier.padding(top = 20.dp),
        onClick = {},
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(10.dp),
        shape = RoundedCornerShape(10.dp)
    ) {
        Column {
            Row (
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ){
                val formattedTime by mainScreenViewModel.formattedTime.collectAsState()
                Column(
                    modifier = Modifier.padding(start = 10.dp, bottom = 10.dp, end = 10.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    val formattedDate by mainScreenViewModel.formattedDate.collectAsState()
                    var previousTime by remember { mutableStateOf("") }
                    var currentDate by remember { mutableStateOf(LocalDate.now()) }
                    LaunchedEffect(Unit){
                        while (true){
                            previousTime = formattedTime
                            mainScreenViewModel.updateFormattedTime()
                            if (formattedTime.subSequence(0,5) == "23:59" && LocalDate.now() != currentDate) {
                                currentDate = LocalDate.now()
                                mainScreenViewModel.updateFormattedDate()
                            }
                            delay(1000)
                        }
                    }
                    Text(
                        modifier = Modifier.padding(start = 3.dp, top = 7.dp),
                        text = formattedDate,
                        style = MaterialTheme.typography.labelLarge,
                        color = LocalContentColor.current.copy(alpha = 0.87f),
                        maxLines = 1,
                        softWrap = false,
                        textAlign = TextAlign.Center
                    )
                    AnimatedTimer(formattedTime, previousTime)
                    Text(
                        text = "Remaining time to next prayer ->",
                        modifier = Modifier.padding(start = 3.dp, top = 1.dp),
                        style = MaterialTheme.typography.labelLarge,
                        color = LocalContentColor.current.copy(alpha = 0.6f),
                        maxLines = 1,
                        softWrap = false,
                        textAlign = TextAlign.Center
                    )
                }

                val currentTime = remember { formattedTime }

                val prayTimes by mainScreenViewModel.dailyPrayTimes.collectAsState()
                prayTimes.data?.let {
                    TimeCounter(
                        Modifier
                            .weight(1f)
                            .size(100.dp), currentTime,it
                    )
                }
            }
            HorizontalDivider(Modifier.padding(15.dp))
            val dailyPrayTime by mainScreenViewModel.dailyPrayTimes.collectAsState()

            when(dailyPrayTime.status){
                Status.SUCCESS->{
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(1f)
                            .padding(top = 10.dp)
                    ) {
                        PrayTimesRow(dailyPrayTime.data!!)
                    }
                }
                Status.ERROR -> {
                    Text(text = dailyPrayTime.message.toString())
                }
                Status.LOADING -> {
                   // CircularProgressIndicator()
                }
            }
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AnimatedTimer(formattedTime : String,previousTime : String){
    Row {
        formattedTime.forEachIndexed { index, c ->
            val oldChar = previousTime.getOrNull(index)
            val char = if (oldChar == c) previousTime[index] else c
            AnimatedContent(
                targetState = char,
                transitionSpec = {
                    slideInVertically(tween(500)){fullWidth -> -fullWidth } + fadeIn(tween(500)) with
                            slideOutVertically(tween(500)){fullWidth -> fullWidth  } + fadeOut(tween(500))
                }, label = ""
            ) {
                Text(
                    modifier = Modifier.padding(top = 1.dp),
                    text = it.toString(),
                    style = MaterialTheme.typography.headlineLarge,
                    color = LocalContentColor.current.copy(alpha = 0.87f),
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    softWrap = false,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun RowScope.PrayTimesRow(prayTime : PrayTimes) {
    val prayList = prayTime.toList()
    prayList.forEach { prayPair ->
        val icon = when(prayPair.first){
            "Morning" -> painterResource(R.drawable.morning)
            "Noon" -> painterResource(R.drawable.noon)
            "Afternoon" -> painterResource(R.drawable.afternoon)
            "Evening" -> painterResource(R.drawable.evening)
            "Night" -> painterResource(R.drawable.night)
            else -> painterResource(R.drawable.morning)
        }
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = prayPair.first,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1,
                softWrap = false,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.W600,
            )
            Icon(
                modifier = Modifier
                    .padding(top = 5.dp)
                    .size(35.dp), painter = icon,contentDescription = prayPair.first)
            Text(
                modifier = Modifier.padding(top = 5.dp, bottom = 8.dp),
                text = prayPair.second,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1,
                softWrap = false,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.W600,
            )
        }
    }

}


@Composable
fun AddressBar() {
    val mainScreenViewModel : MainScreenViewModel = hiltViewModel()

    Box (modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterStart) {
        var isExpanded by remember { mutableStateOf(false) }
        Card (
            onClick = {
            isExpanded = !isExpanded
            },
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(10.dp),
            shape = RoundedCornerShape(10.dp)
        )
        {
            Row(
                Modifier.padding(start = 7.dp, end = 7.dp, top = 3.dp, bottom = 3.dp)
            ) {

                Icon(
                    imageVector = Icons.Outlined.LocationOn,
                    contentDescription = "Location Icon",
                    tint = IconColor
                )
                val prayTimes by mainScreenViewModel.dailyPrayTimes.collectAsState()
                val currentAddress by remember {
                    derivedStateOf {
                        prayTimes.data?.toAddress()
                    }
                }
                val text by remember {
                    derivedStateOf {
                        when(currentAddress){
                            null -> "Location"
                            else -> if (!isExpanded) {
                                currentAddress?.city + ", " + currentAddress?.country
                            }else{
                                currentAddress?.fullAddress?:""
                            }
                        }
                    }
                }
                Text(
                    modifier = Modifier
                        .animateContentSize(tween(700))
                        .padding(start = 3.dp, top = 1.dp, end = 5.dp)
                        .basicMarquee(),
                    text = text,
                    style = MaterialTheme.typography.labelLarge,
                    color = LocalContentColor.current.copy(alpha = 0.87f),
                    maxLines = 1,
                    softWrap = false,
                    textAlign = TextAlign.Center
                )
            }
        }

        AnimatedVisibility(
            modifier = Modifier
                .padding(end = 8.dp, bottom = 3.dp)
                .align(Alignment.CenterEnd)
                .size(25.dp)
            ,
            visible = !isExpanded,
            enter = slideInVertically(tween(500)){fullWidth -> fullWidth *2 } + fadeIn(tween(500)),
            exit = slideOutVertically(tween(500)){fullWidth -> fullWidth *2 } + fadeOut(tween(500))
        ) {
            Icon(
                imageVector = Icons.Outlined.Notifications,
                contentDescription = "Notification Icon"
            )
        }

    }
}

@Composable
fun PrayerBar() {
    Box (modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterStart) {
        Card (
            onClick = {},
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(10.dp),
            shape = RoundedCornerShape(10.dp)
        )
        {
            Row(
                modifier = Modifier
                    .fillMaxWidth(1f)
                    .padding(start = 15.dp, end = 15.dp, top = 10.dp, bottom = 10.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Favorite,
                    contentDescription = "Location Icon",
                    tint = IconColor
                )
                Text(
                    modifier = Modifier.padding(start = 3.dp, top = 1.dp),
                    text = "Start your day with these prayers",
                    style = MaterialTheme.typography.labelLarge,
                    color = LocalContentColor.current.copy(alpha = 0.87f),
                    maxLines = 1,
                    softWrap = false,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    modifier = Modifier
                        .background(
                            shape = RoundedCornerShape(15.dp),
                            color = IconBackGroundColor
                        )
                        .size(25.dp),
                    imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowRight,
                    contentDescription = "Arrow Icon"
                )
            }

        }

    }
}

@SuppressLint("DefaultLocale")
@Composable
fun TimeCounter(modifier: Modifier = Modifier,currentTime: String,prayTime: PrayTimes) {

    var isClicked by remember { mutableStateOf(false) }
    val rotationY = animateFloatAsState(
        targetValue = if (isClicked) 180f else 0f,
        animationSpec = tween(1000, easing = EaseInOutQuad),
        label = ""
    )
    val scale = animateFloatAsState(
        targetValue = if (isClicked) 0.99f else 1.01f,
        keyframes {
            durationMillis = 1500
            0.7f at 150
            0.9f at 300
            1.2f at 750
            0.7f at 1000
            1f at 1500
        },
        label = ""
    )
    val prayTimeList = prayTime.toList().map { it.second }
    val currentSeconds = currentTime.convertTimeToSeconds()

    var nextTimeIndex = prayTimeList.indexOfFirst { it.convertTimeToSeconds() > currentSeconds }
    if (nextTimeIndex == -1) {
        nextTimeIndex = 0
    }
    val nextTime = prayTimeList[nextTimeIndex]
    val previousTime = if (nextTimeIndex == 0) prayTimeList.last() else prayTimeList[nextTimeIndex - 1]

    var totalSeconds = nextTime.convertTimeToSeconds() - previousTime.convertTimeToSeconds()
    if (totalSeconds < 0) {
        totalSeconds += 24 * 3600
    }

    var elapsedSeconds by remember { mutableIntStateOf(currentSeconds - previousTime.convertTimeToSeconds()) }
    if(elapsedSeconds < 0) elapsedSeconds += 24 * 3600

    var remainingSeconds by remember { mutableIntStateOf(totalSeconds - elapsedSeconds) }

    val sweepAngle by remember {
        derivedStateOf {
            (remainingSeconds.toFloat() / totalSeconds.toFloat()) * 360f
        }
    }

    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            elapsedSeconds++
            remainingSeconds--
            if (elapsedSeconds >= totalSeconds) {
                elapsedSeconds -= totalSeconds
            }
            if(remainingSeconds < 0) remainingSeconds += totalSeconds
        }
    }

    val formattedTime = String.format("%02d:%02d:%02d", remainingSeconds / 3600, (remainingSeconds / 60) % 60, remainingSeconds % 60)

    Box(modifier = modifier
        .noRippleClickable {
            isClicked = !isClicked
        }
        .graphicsLayer {
            this.rotationY = rotationY.value
            this.scaleY = scale.value
            this.scaleX = scale.value
        }, contentAlignment = Alignment.Center) {

        Text(modifier = Modifier.graphicsLayer {
            this.rotationY = rotationY.value
        }, text = formattedTime, fontSize = 18.sp)
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val radius = size.minDimension / 2f - 20f
            val strokeWidth = 15f
            val circleRadius = 17f

            drawCircle(
                color = IconBackGroundColor,
                center = center,
                radius = radius,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )

            drawArc(
                brush = Brush.linearGradient(
                    colors = listOf(IconColor, LightGreen),
                    tileMode = TileMode.Repeated
                ),
                startAngle = -90f,
                sweepAngle = sweepAngle,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                topLeft = Offset(center.x - radius, center.y - radius),
                size = androidx.compose.ui.geometry.Size(radius * 2, radius * 2)
            )

            val angleRadians = (-90f + sweepAngle) * (PI / 180f).toFloat()
            val circleCenterX = center.x + radius * cos(angleRadians)
            val circleCenterY = center.y + radius * sin(angleRadians)

            drawCircle(
                brush = Brush.linearGradient(
                    colors = listOf(IconColor, LightGreen),
                    tileMode = TileMode.Repeated
                ),
                radius = circleRadius,
                center = Offset(circleCenterX, circleCenterY)
            )
        }
    }
}
