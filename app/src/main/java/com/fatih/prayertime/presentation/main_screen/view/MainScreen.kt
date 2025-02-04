package com.fatih.prayertime.presentation.main_screen.view

import android.Manifest
import android.annotation.SuppressLint
import android.app.TimePickerDialog
import android.content.Context
import android.content.DialogInterface.OnDismissListener
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateColorAsState
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
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.exyte.animatednavbar.utils.noRippleClickable
import com.fatih.prayertime.R
import com.fatih.prayertime.domain.model.GlobalAlarm
import com.fatih.prayertime.domain.model.PrayTimes
import com.fatih.prayertime.presentation.main_activity.viewmodel.AppViewModel
import com.fatih.prayertime.presentation.main_screen.viewmodel.MainScreenViewModel
import com.fatih.prayertime.util.NetworkState
import com.fatih.prayertime.util.Status
import com.fatih.prayertime.util.convertTimeToSeconds
import com.fatih.prayertime.util.localDateTime
import com.fatih.prayertime.util.toAddress
import com.fatih.prayertime.util.toList
import kotlinx.coroutines.delay
import org.threeten.bp.Instant
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import org.threeten.bp.Year
import org.threeten.bp.YearMonth
import org.threeten.bp.ZoneId
import org.threeten.bp.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale

import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun MainScreen(appViewModel: AppViewModel,bottomPaddingValue : Dp) {
    val mainScreenViewModel : MainScreenViewModel = hiltViewModel()
    val scrollState = rememberScrollState()
    GetLocationInformation(mainScreenViewModel,appViewModel)
    var isVisible by remember { mutableStateOf(false) }
    val haptic = LocalHapticFeedback.current

    LaunchedEffect(Unit){
        isVisible = true
    }
    Column(modifier = Modifier.verticalScroll(scrollState, enabled = true) ){
        AnimatedVisibility(
            visible = isVisible,
            enter = slideInVertically(animationSpec = tween(1000)) + fadeIn(),
            exit = slideOutVertically(animationSpec = tween(1000)) + fadeOut()
        ){
            AddressBar(haptic)
        }
        AnimatedVisibility(
            visible = isVisible,
            enter = slideInVertically(animationSpec = tween(1000)) + fadeIn(),
            exit = slideOutVertically(animationSpec = tween(1000)) + fadeOut()
        ){
            PrayerBar(haptic)
        }
        AnimatedVisibility(
            visible = isVisible,
            enter = slideInHorizontally(tween(500)) + fadeIn(),
            exit = slideOutHorizontally(tween(500)) + fadeOut()
        ){
            PrayScheduleCompose(haptic)
        }
        AnimatedVisibility(
            visible = isVisible,
            enter = slideInHorizontally(tween(500)) { x ->
                x / 2
            } + fadeIn(),
            exit = slideOutHorizontally(tween(500)) + fadeOut()
        ){
            PrayNotificationCompose(mainScreenViewModel,appViewModel,haptic)
        }
        AnimatedVisibility(
            visible = isVisible,
            enter = expandIn(expandFrom = Alignment.BottomCenter) + fadeIn(tween(1000)),
            exit = shrinkOut(shrinkTowards = Alignment.TopCenter) + fadeOut(tween(1000))
        ){
            DailyPrayCompose(haptic)
        }
        Spacer(
            modifier = Modifier.height(25.dp + bottomPaddingValue)
        )
    }

}

@Composable
fun GetLocationInformation(mainScreenViewModel: MainScreenViewModel, appViewModel: AppViewModel){
    val permissionGranted by appViewModel.isLocationPermissionGranted.collectAsState()
    var isLocationTracking by rememberSaveable { mutableStateOf(false) }
    val networkState by appViewModel.networkState.collectAsState()
    LaunchedEffect (key1 = networkState, key2 = permissionGranted){
        if (!isLocationTracking && permissionGranted && networkState == NetworkState.Connected){
            mainScreenViewModel.trackLocationAndUpdatePrayTimes()
            isLocationTracking = true
        }
        if (permissionGranted){
            mainScreenViewModel.getMonthlyPrayTimesFromAPI(Year.now().value, YearMonth.now().monthValue,null)
        }
    }
}


@Composable
fun DailyPrayCompose(haptic: HapticFeedback) {
    Card(
        modifier = Modifier.padding(top = 20.dp),
        onClick = {
            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
        },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        elevation = CardDefaults.cardElevation(10.dp),
        shape = RoundedCornerShape(10.dp)
    )  {
        Column(modifier = Modifier.padding(top = 10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    modifier = Modifier.padding(start = 10.dp),
                    text = "Daily Prayer",
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    softWrap = false,
                )
                Spacer(Modifier.weight(1f))
                Card(
                    modifier = Modifier.padding(end = 10.dp),
                    onClick = {},
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                    elevation = CardDefaults.cardElevation(10.dp),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        modifier = Modifier.padding(4.dp),
                        text = "See All",
                        style = MaterialTheme.typography.labelMedium,
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
                                .padding(10.dp)
                                .weight(1f),
                            onClick = {},
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
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
                                        .basicMarquee(iterations = Int.MAX_VALUE),
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

}

@Composable
fun PrayNotificationCompose(
    mainScreenViewModel: MainScreenViewModel,
    appViewModel: AppViewModel,
    haptic: HapticFeedback
) {
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
            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            rotate = !rotate
        },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        elevation = CardDefaults.cardElevation(10.dp),
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            val isNotificationPermissionGranted by appViewModel.isNotificationPermissionGranted
            val context = LocalContext.current
            val permissionLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestPermission()
            ) { isGranted ->
                if (!isGranted) {
                    Toast.makeText(context, "Bildirim izni reddedildi.Ayarlardan bildirim izinlerini açın", Toast.LENGTH_LONG).show()
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = Uri.fromParts("package", context.packageName, null)
                    }
                    context.startActivity(intent)
                }else{
                    appViewModel.checkNotificationPermission()
                }
            }
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
                    maxLines = 1,
                    softWrap = false,
                    textAlign = TextAlign.Center,
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
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                elevation = CardDefaults.cardElevation(5.dp),
                shape = RoundedCornerShape(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    val globalAlarmList by mainScreenViewModel.globalAlarmList.collectAsState()
                    if (globalAlarmList != null) {
                        var showDialog by rememberSaveable { mutableStateOf(false) }
                        val selectedGlobalAlarm by mainScreenViewModel.selectedGlobalAlarm.collectAsState()
                        var initialHour by rememberSaveable { mutableIntStateOf(0) }
                        var initialMinutes by rememberSaveable { mutableIntStateOf(0) }
                        ClassicTimePicker(

                            initialHour = initialHour,
                            initialMinutes = initialMinutes,
                            onTimeSelect = { alarmTimeLong,alarmTimeString,offset ->
                            if (selectedGlobalAlarm == null) return@ClassicTimePicker
                            mainScreenViewModel.updateGlobalAlarm(
                                selectedGlobalAlarm!!.alarmType,
                                alarmTimeLong,
                                alarmTimeString,
                                !selectedGlobalAlarm!!.isEnabled,
                                offset,
                            )
                        }, onDismissListener = {
                            showDialog = false
                        },showDialog)
                        globalAlarmList!!.forEachIndexed { index, globalAlarm ->
                            Column (
                                modifier = Modifier
                                    .weight(1f)
                                    .size(70.dp)
                                    .padding(vertical = 10.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable {
                                        if (isNotificationPermissionGranted) {
                                            if (globalAlarm.isEnabled){
                                                mainScreenViewModel.updateGlobalAlarm(
                                                    globalAlarm.alarmType,
                                                    0L,
                                                    "16-01-2025 00:00",
                                                    false,
                                                    0L)
                                                return@clickable
                                            }
                                            val initialTimeValues = mainScreenViewModel.getHourAndMinuteFromIndex(index)
                                            showDialog = true
                                            mainScreenViewModel.setSelectedGlobalAlarm(globalAlarm)
                                            initialHour = initialTimeValues.first
                                            initialMinutes = initialTimeValues.second
                                        } else {
                                            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                        }
                                    },
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                AlarmComposable(globalAlarm)
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
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    if (isNotificationPermissionGranted) {
                        mainScreenViewModel.updateAllGlobalAlarm()
                    } else {
                        permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                },
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
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
                    maxLines = 1,
                    softWrap = false,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.W500
                )
            }
        }
    }
}

@Composable
fun ClassicTimePicker(
    initialHour : Int,
    initialMinutes : Int,
    onTimeSelect : (Long,String,Long) -> Unit,
    onDismissListener: OnDismissListener,
    showDialog : Boolean = false) {
    val context = LocalContext.current
    val calendar = remember { Calendar.getInstance() }
    val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm", Locale.getDefault())
    calendar.set(Calendar.HOUR_OF_DAY,initialHour)
    calendar.set(Calendar.MINUTE,initialMinutes)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)
    val initialTimeInMillis = calendar.timeInMillis
    val timePickerDialog = TimePickerDialog(
        context,
        { _, hourOfDay, minute ->
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
            calendar.set(Calendar.MINUTE, minute)
            val selectedTimeInMillis = calendar.timeInMillis
            val selectedTimeString = LocalDateTime.ofInstant(Instant.ofEpochMilli(selectedTimeInMillis), ZoneId.systemDefault()).format(formatter)
            val offset = selectedTimeInMillis - initialTimeInMillis
            onTimeSelect(selectedTimeInMillis,selectedTimeString,offset)
        },
        calendar[Calendar.HOUR_OF_DAY],
        calendar[Calendar.MINUTE],
        true
    ).apply {
        setOnDismissListener(onDismissListener)
    }

    LaunchedEffect(showDialog) {
        if (showDialog) {
            timePickerDialog.show()
        }
    }

}


@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AlarmComposable(globalAlarm: GlobalAlarm) {
    val iconColor = animateColorAsState(
        targetValue = if (globalAlarm.isEnabled) MaterialTheme.colorScheme.primary else Color.Red,
        animationSpec = tween(1000), label = ""
    )
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
            tint = iconColor.value,
            painter = it,
            contentDescription = "Check Circle",
        )
    }

    Text(
        text = globalAlarm.alarmType,
        style = MaterialTheme.typography.titleSmall,
        maxLines = 1,
        softWrap = false,
        textAlign = TextAlign.Center,
        fontWeight = FontWeight.W600
    )
}

@SuppressLint("NewApi")
@Composable
fun PrayScheduleCompose(haptic: HapticFeedback) {
    val mainScreenViewModel : MainScreenViewModel = hiltViewModel()
    Card(
        modifier = Modifier.padding(top = 20.dp),
        onClick = {
            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
        },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
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
                        maxLines = 1,
                        softWrap = false,
                        textAlign = TextAlign.Center
                    )
                    AnimatedTimer(formattedTime, previousTime)
                    Text(
                        text = "Remaining time to next prayer ->",
                        modifier = Modifier.padding(start = 3.dp, top = 1.dp),
                        style = MaterialTheme.typography.labelLarge,
                        maxLines = 1,
                        softWrap = false,
                        textAlign = TextAlign.Center
                    )
                }


                val prayTimes by mainScreenViewModel.dailyPrayTimes.collectAsState()
                prayTimes.data?.let {
                    TimeCounter(
                        Modifier
                            .weight(1f)
                            .size(100.dp), formattedTime,it
                    )
                }
            }
            HorizontalDivider(Modifier.padding(15.dp))
            val dailyPrayTime by mainScreenViewModel.dailyPrayTimes.collectAsState()
            when(dailyPrayTime.status){
                Status.SUCCESS->{
                    Card(
                        modifier = Modifier
                            .padding(start = 10.dp, end = 10.dp, bottom = 10.dp)
                        ,
                        onClick = {},
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                        elevation = CardDefaults.cardElevation(10.dp),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(1f)
                                .padding(top = 10.dp)
                        ) {
                            if (dailyPrayTime.data != null){
                                val localDateTimeNow = LocalDateTime.now()
                                val index = when{
                                    localDateTimeNow.isBefore(dailyPrayTime.data!!.localDateTime(dailyPrayTime.data!!.morning)) -> 0
                                    localDateTimeNow.isBefore(dailyPrayTime.data!!.localDateTime(dailyPrayTime.data!!.noon)) -> 1
                                    localDateTimeNow.isBefore(dailyPrayTime.data!!.localDateTime(dailyPrayTime.data!!.afternoon)) -> 2
                                    localDateTimeNow.isBefore(dailyPrayTime.data!!.localDateTime(dailyPrayTime.data!!.evening)) -> 3
                                    localDateTimeNow.isBefore(dailyPrayTime.data!!.localDateTime(dailyPrayTime.data!!.night)) -> 4
                                    else -> 0
                                }
                                println(index)
                                PrayTimesRow(dailyPrayTime.data!!, index)

                            }
                        }
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
fun RowScope.PrayTimesRow(prayTime : PrayTimes,index : Int) {
    Log.d("PrayTimesRow",index.toString())
    val prayList = prayTime.toList()
    prayList.forEachIndexed { currentIndex ,prayPair->
        val icon = when(prayPair.first){
            "Morning" -> painterResource(R.drawable.morning)
            "Noon" -> painterResource(R.drawable.noon)
            "Afternoon" -> painterResource(R.drawable.afternoon)
            "Evening" -> painterResource(R.drawable.evening)
            "Night" -> painterResource(R.drawable.night)
            else -> painterResource(R.drawable.morning)
        }
        val color = if (currentIndex == index) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSecondaryContainer
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
                color = color
            )
            Icon(
                modifier = Modifier
                    .padding(top = 5.dp)
                    .size(35.dp), painter = icon,contentDescription = prayPair.first,
                tint = color
            )
            Text(
                modifier = Modifier.padding(top = 5.dp, bottom = 8.dp),
                text = prayPair.second,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                softWrap = false,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.W500,
                color = color
            )
        }
    }

}


@Composable
fun AddressBar(haptic: HapticFeedback) {
    val mainScreenViewModel : MainScreenViewModel = hiltViewModel()

    Box (modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterStart) {
        var isExpanded by remember { mutableStateOf(false) }
        Card (
            onClick = {
                isExpanded = !isExpanded
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            },
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
            ),
            elevation = CardDefaults.cardElevation(10.dp),
            shape = RoundedCornerShape(10.dp)
        )
        {
            Row(
                Modifier.padding(start = 7.dp, end = 7.dp, top = 3.dp, bottom = 3.dp)
                    .background(color = Color.Transparent)
            ) {

                Icon(
                    imageVector = Icons.Outlined.LocationOn,
                    contentDescription = "Location Icon",
                    tint = MaterialTheme.colorScheme.primary
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
                contentDescription = "Notification Icon",
                tint = MaterialTheme.colorScheme.primary
            )
        }

    }
}

@Composable
fun PrayerBar(haptic: HapticFeedback) {
    Box (modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterStart) {
        Card (
            onClick = {
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            },
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
            elevation = CardDefaults.cardElevation(10.dp),
            shape = RoundedCornerShape(10.dp)
        )
        {
            Row(
                modifier = Modifier
                    .fillMaxWidth(1f)
                    .padding(start = 15.dp, end = 15.dp, top = 10.dp, bottom = 10.dp)
                    .background(color = Color.Transparent)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Favorite,
                    contentDescription = "Location Icon",
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    modifier = Modifier.padding(start = 3.dp, top = 1.dp),
                    text = "Start your day with these prayers",
                    style = MaterialTheme.typography.labelLarge,
                    maxLines = 1,
                    softWrap = false,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    modifier = Modifier
                        .background(
                            shape = RoundedCornerShape(15.dp),
                            color = MaterialTheme.colorScheme.background
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

    var elapsedSeconds = currentSeconds - previousTime.convertTimeToSeconds()
    if(elapsedSeconds < 0) elapsedSeconds += 24 * 3600

    var remainingSeconds =totalSeconds - elapsedSeconds

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
            if(remainingSeconds < 0) {
                remainingSeconds += totalSeconds
            }
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

        Text(
            modifier = Modifier.graphicsLayer
            {
            this.rotationY = rotationY.value
            },
            text = formattedTime, fontSize = 18.sp
        )
        val primaryColor = MaterialTheme.colorScheme.primary
        val secondaryColor = MaterialTheme.colorScheme.secondary
        val bgColor = MaterialTheme.colorScheme.background
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val radius = size.minDimension / 2f - 20f
            val strokeWidth = 15f
            val circleRadius = 17f

            drawCircle(
                color = bgColor,
                center = center,
                radius = radius,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )

            drawArc(
                brush = Brush.linearGradient(
                    colors = listOf(primaryColor, secondaryColor),
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
                    colors = listOf(primaryColor, secondaryColor),
                    tileMode = TileMode.Repeated
                ),
                radius = circleRadius,
                center = Offset(circleCenterX, circleCenterY)
            )
        }
    }
}

@Composable
fun PrayerMethodSelector(
    methods: Map<String, Int>,
    onMethodSelected: (String) -> Unit
) {
    val expanded = remember { mutableStateOf(false) }
    val selectedMethod = remember { mutableStateOf(methods.keys.first()) }

    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        Text(
            text = "Select Prayer Time Calculation Method",
            modifier = Modifier.clickable { expanded.value = true }
        )
        Text(text = selectedMethod.value, modifier = Modifier.padding(top = 8.dp))

        DropdownMenu(
            expanded = expanded.value,
            onDismissRequest = { expanded.value = false }
        ) {
            methods.keys.forEach { method ->
                DropdownMenuItem(
                    text = {
                        Text(text = method)
                    },
                    onClick = {
                        selectedMethod.value = method
                        expanded.value = false
                        onMethodSelected(method)
                    }
                )
            }
        }
    }
}

fun vibratePhone(context: Context) {
    val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
        vibratorManager.defaultVibrator
    } else {
        context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }
    if (vibrator.hasVibrator()) {
        val vibrationEffect = VibrationEffect.createOneShot(35, VibrationEffect.DEFAULT_AMPLITUDE)
        vibrator.vibrate(vibrationEffect)
    }
}

