package com.fatih.prayertime.presentation.main_screen

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateColor
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.EaseInOutQuad
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import com.fatih.prayertime.util.composables.TitleView
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells.Fixed
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.exyte.animatednavbar.utils.noRippleClickable
import com.fatih.prayertime.R
import com.fatih.prayertime.data.remote.dto.duadto.DuaCategoryData
import com.fatih.prayertime.data.remote.dto.duadto.DuaCategoryDetail
import com.fatih.prayertime.domain.model.PrayerAlarm
import com.fatih.prayertime.domain.model.PrayTimes
import com.fatih.prayertime.presentation.main_screen.component.WeatherCard
import com.fatih.prayertime.util.extensions.convertTimeToSeconds
import com.fatih.prayertime.util.extensions.localDateTime
import com.fatih.prayertime.util.extensions.toAddress
import com.fatih.prayertime.util.model.enums.PrayTimesString
import com.fatih.prayertime.util.composables.ErrorView
import com.fatih.prayertime.util.composables.LoadingView
import com.fatih.prayertime.util.config.NavigationConfig.screens
import com.fatih.prayertime.util.extensions.navigateToScreen
import com.fatih.prayertime.util.extensions.toPrayTimeInfoList
import com.fatih.prayertime.util.extensions.toTimeList
import com.fatih.prayertime.util.model.event.MainScreenEvent
import kotlinx.coroutines.delay
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime

import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import com.fatih.prayertime.util.utils.getLocalizedString
import com.fatih.prayertime.util.composables.LottieAnimationSized

@Composable
fun MainScreen( modifier: Modifier,navController: NavController, mainScreenViewModel: MainScreenViewModel = hiltViewModel()) {
    LottieAnimationSized (
        lottieFile = "splash_screen_anim.lottie",
        autoPlay = true,
        loop = true,
        speed = 1.75f,
        width = 200,
        height = 200,
        lottieAnimDuration = 1000
    ) {
        var showAlarmDialog by remember { mutableStateOf(false) }
        val scrollState = rememberScrollState()
        var isVisible by remember { mutableStateOf(false) }
        val haptic = LocalHapticFeedback.current
        val selectedDuaState by mainScreenViewModel.selectedDuaState.collectAsStateWithLifecycle()

        LaunchedEffect(Unit){
            isVisible = true
        }
        AnimatedVisibility(
            visible = selectedDuaState.isVisible && selectedDuaState.dua != null,
            enter = fadeIn(tween(1000)) + slideInVertically(),
            exit = fadeOut(tween(1000)) + slideOutVertically()
        ) {
            FancyDuaDialog(
                duaDetail = selectedDuaState.dua!!,
                onDismiss = {
                    mainScreenViewModel.onEvent(MainScreenEvent.HideDuaDialog)
                }
            )
        }
        AnimatedVisibility(
            visible = showAlarmDialog,
            enter = fadeIn() + slideInVertically(),
            exit = fadeOut() + slideOutVertically()
        ) {
            GlobalAlarmsDialog(mainScreenViewModel){
                showAlarmDialog = false
            }
        }

        Column(modifier = modifier.verticalScroll(scrollState, enabled = true) ){
            AddressBar(haptic,mainScreenViewModel)

            PrayerBar(haptic){
                mainScreenViewModel.onEvent(MainScreenEvent.ShowDuaDialog)
            }

            WeatherCard(
                mainScreenViewModel
            )
            PrayScheduleCompose(haptic)
            PrayNotificationCompose(mainScreenViewModel,haptic){
                showAlarmDialog = true
            }
            DailyPrayCompose(haptic,mainScreenViewModel ){ categoryId ->
                val route = screens[4].route.replace("{categoryId}","$categoryId")
                navController.navigateToScreen(route)
            }
            Spacer(
                modifier = Modifier.height(25.dp)
            )
        }

    }
    TitleView(stringResource(R.string.main_view))

}

@Composable
fun GlobalAlarmsDialog(mainScreenViewModel: MainScreenViewModel, onDismiss: () -> Unit) {
    val globalAlarmList by mainScreenViewModel.prayerAlarmList.collectAsState()

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(8.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = stringResource(R.string.alarms),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f))
                globalAlarmList?.forEach { globalAlarm ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Outlined.Notifications,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = stringResource(PrayTimesString.fromString(globalAlarm.alarmType).stringResId),
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = globalAlarm.alarmTimeString,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                            }
                        }
                        Switch(
                            enabled = false,
                            checked = globalAlarm.isEnabled,
                            colors = SwitchDefaults.colors(
                                disabledUncheckedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
                                disabledCheckedBorderColor = MaterialTheme.colorScheme.primary,
                                disabledCheckedThumbColor = MaterialTheme.colorScheme.primary,
                                disabledUncheckedThumbColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 1f),
                            ),
                            onCheckedChange = {

                            }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.align(Alignment.End),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text(text = stringResource(R.string.close))
                }
            }
        }
    }
}


@Composable
fun DailyPrayCompose(haptic: HapticFeedback,mainScreenViewModel: MainScreenViewModel,onDuaCategoryClick : (Int) -> Unit) {
    val duaCategoryList = mainScreenViewModel.duaCategoryList
    AnimatedVisibility(
        visible = duaCategoryList != null && duaCategoryList.isNotEmpty(),
    ) {
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
                        text = stringResource(R.string.daily_prayer),
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        softWrap = false,
                    )
                    Spacer(Modifier.weight(1f))
                    Card(
                        modifier = Modifier
                            .padding(10.dp)
                            .height(25.dp),
                        onClick = {},
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                        elevation = CardDefaults.cardElevation(10.dp),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            modifier = Modifier.padding(4.dp),
                            text = stringResource(R.string.see_all),
                            style = MaterialTheme.typography.labelMedium,
                            maxLines = 1,
                            softWrap = false,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.W600,
                        )
                    }
                }
                LazyVerticalGrid(
                    columns = Fixed(3),
                    modifier = Modifier.height(220.dp)
                )  {
                    items(duaCategoryList!!) { item ->
                        DuaCategoryCardCompose(item,onDuaCategoryClick)
                    }
                }
            }
        }
    }
}

@Composable
fun PrayNotificationCompose(
    mainScreenViewModel: MainScreenViewModel,
    haptic: HapticFeedback,
    onShowAlarmDialog : () -> Unit
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
            val isNotificationPermissionGranted by mainScreenViewModel.isNotificationPermissionGranted.collectAsStateWithLifecycle()
            val context = LocalContext.current
            val notificationStr = stringResource(R.string.notification_permission_denied)
            val permissionLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestPermission()
            ) { isGranted ->
                if (!isGranted) {
                    Toast.makeText(context, notificationStr , Toast.LENGTH_LONG).show()
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = Uri.fromParts("package", context.packageName, null)
                    }
                    context.startActivity(intent)
                }else{
                    mainScreenViewModel.checkNotificationPermission()
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
                    imageVector = ImageVector.vectorResource(R.drawable.alarm_icon),
                    contentDescription = stringResource(R.string.alarm_icon)
                )
                Text(
                    modifier = Modifier.padding(start = 5.dp),
                    text = stringResource(R.string.prayer_tracker),
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    softWrap = false,
                    textAlign = TextAlign.Center,
                )
                Spacer(
                    modifier = Modifier.weight(1f)
                )
                Card(
                    modifier = Modifier
                        .padding(3.dp)
                        .height(25.dp),
                    onClick = {
                        onShowAlarmDialog()
                    },
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                    elevation = CardDefaults.cardElevation(10.dp),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        modifier = Modifier.padding(4.dp),
                        text = stringResource(R.string.see_all),
                        style = MaterialTheme.typography.labelMedium,
                        maxLines = 1,
                        softWrap = false,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.W600,
                    )
                }

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
                val prayerAlarmList by mainScreenViewModel.prayerAlarmList.collectAsState()
                LazyRow {
                    prayerAlarmList?.let { prayerAlarms->
                        itemsIndexed(prayerAlarms) {index, prayerAlarm->
                            PrayerAlarmCard(prayerAlarm){
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                updatePrayAlarmsOrCheckPermission(isNotificationPermissionGranted,prayerAlarm,index,mainScreenViewModel,permissionLauncher)
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
                        mainScreenViewModel.updateAllGlobalAlarm(true)
                    } else {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        }
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
                    text = stringResource(R.string.pray_together),
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

@SuppressLint("NewApi")
@Composable
fun PrayScheduleCompose(haptic: HapticFeedback) {
    val mainScreenViewModel : MainScreenViewModel = hiltViewModel()
    val prayerState by mainScreenViewModel.prayerUiState.collectAsStateWithLifecycle()

    Card(
        modifier = Modifier.padding(top = 12.dp),
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
            TimerRow(mainScreenViewModel,prayerState.prayTimes)
            HorizontalDivider(Modifier.padding(15.dp))
            when{
                prayerState.prayTimes != null->{
                    PrayTimesRowHeader(prayerState.prayTimes)
                }
                prayerState.error != null -> {
                   ErrorView(prayerState.error?:"Error occurred while fetching pray times"){}
                }
                prayerState.isLoading -> {
                   LoadingView()
                }
            }
        }
    }
}

@Composable
fun TimerRow(mainScreenViewModel: MainScreenViewModel, dailyPrayTime: PrayTimes?) {
    val formattedDate by mainScreenViewModel.formattedDate.collectAsState()
    val formattedTime by mainScreenViewModel.formattedTime.collectAsState()
    Row (
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ){
        Column(
            modifier = Modifier.padding(start = 10.dp, bottom = 10.dp, end = 10.dp),
            verticalArrangement = Arrangement.Center
        ) {
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
                text = stringResource(R.string.remaining_time_text),
                modifier = Modifier.padding(start = 3.dp, top = 1.dp),
                style = MaterialTheme.typography.labelLarge,
                maxLines = 1,
                softWrap = false,
                textAlign = TextAlign.Center
            )
        }

        dailyPrayTime?.let {
            TimeCounter(
                Modifier
                    .weight(1f)
                    .size(100.dp), formattedTime,it
            )
        }
    }
}

@Composable
fun PrayerAlarmCard(prayerAlarm: PrayerAlarm,onCardClick : () -> Unit) {

    val iconColor = animateColorAsState(
        targetValue = if (prayerAlarm.isEnabled) MaterialTheme.colorScheme.primary else Color.Red,
        animationSpec = tween(1000), label = ""
    )
    val isChecked = rememberSaveable(prayerAlarm.isEnabled) { prayerAlarm.isEnabled }
    val iconDrawable = if (isChecked) ImageVector.vectorResource(R.drawable.check_circle) else ImageVector.vectorResource(R.drawable.cross_icon)

    Column (
        modifier = Modifier
            .size(70.dp)
            .padding(vertical = 10.dp)
            .clip(RoundedCornerShape(10.dp))
            .clickable{onCardClick()},
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AnimatedContent(
            targetState = iconDrawable,
            transitionSpec ={
                scaleIn(tween(1000)) + fadeIn(tween(500)) togetherWith
                        scaleOut(tween(1000))+ fadeOut(tween(500))
            },
            label = ""

        ) {
            Icon(
                modifier = Modifier
                    .padding(top = 3.dp)
                ,
                tint = iconColor.value,
                imageVector = it,
                contentDescription = stringResource(R.string.check_icon),
            )
        }

        Text(
            text = stringResource(PrayTimesString.valueOf(prayerAlarm.alarmType).stringResId),
            style = MaterialTheme.typography.titleSmall,
            maxLines = 1,
            softWrap = false,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.W600
        )
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
                    slideInVertically(tween(500)){fullWidth -> -fullWidth } + fadeIn(tween(500)) togetherWith
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
fun PrayTimesRowHeader(dailyPrayTime : PrayTimes?) {
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
            if (dailyPrayTime != null){
                val localDateTimeNow = LocalDateTime.now()
                val index = when{
                    localDateTimeNow.isBefore(dailyPrayTime.localDateTime(dailyPrayTime.morning)) -> 4
                    localDateTimeNow.isBefore(dailyPrayTime.localDateTime(dailyPrayTime.noon)) -> 0
                    localDateTimeNow.isBefore(dailyPrayTime.localDateTime(dailyPrayTime.afternoon)) -> 1
                    localDateTimeNow.isBefore(dailyPrayTime.localDateTime(dailyPrayTime.evening)) -> 2
                    localDateTimeNow.isBefore(dailyPrayTime.localDateTime(dailyPrayTime.night)) -> 3
                    else -> 4
                }
                PrayTimesRow(dailyPrayTime, index)

            }
        }
    }
}

@Composable
fun RowScope.PrayTimesRow(prayTime: PrayTimes,index: Int) {
    val prayTimeInfoList = prayTime.toPrayTimeInfoList()
    prayTimeInfoList.forEachIndexed { currentIndex ,prayTimeInfo->

        val color = if (currentIndex == index) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSecondaryContainer
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = stringResource(prayTimeInfo.stringRes),
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
                    .size(35.dp),
                painter = rememberAsyncImagePainter(prayTimeInfo.drawable),
                contentDescription = stringResource(prayTimeInfo.stringRes),
                tint = color
            )
            Text(
                modifier = Modifier.padding(top = 5.dp, bottom = 8.dp),
                text = prayTimeInfo.time,
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
fun AddressBar(haptic: HapticFeedback,mainScreenViewModel: MainScreenViewModel) {
    val prayerState by mainScreenViewModel.prayerUiState.collectAsStateWithLifecycle()
    val locationText = stringResource(R.string.location_text)
    val currentAddress by remember(prayerState.prayTimes) {
        derivedStateOf { prayerState.prayTimes?.toAddress() }
    }
    println(prayerState.prayTimes?.fullAddress)
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
                Modifier
                    .padding(start = 7.dp, end = 7.dp, top = 3.dp, bottom = 3.dp)
                    .background(color = Color.Transparent)
            ) {

                Icon(
                    imageVector = Icons.Outlined.LocationOn,
                    contentDescription = stringResource(R.string.location_icon),
                    tint = MaterialTheme.colorScheme.primary
                )
                val text by remember {
                    derivedStateOf {
                        when(currentAddress){
                            null -> locationText
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
                contentDescription = stringResource(R.string.notification_icon),
                tint = MaterialTheme.colorScheme.primary
            )
        }

    }
}

@Composable
fun PrayerBar(haptic: HapticFeedback,onClick : () -> Unit) {
    Box (modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterStart) {
        Card (
            onClick = {
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                onClick()
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
                    contentDescription = stringResource(R.string.location_icon),
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    modifier = Modifier.padding(start = 3.dp, top = 1.dp),
                    text = stringResource(R.string.prayer_bar_text),
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
                    contentDescription = stringResource(R.string.arrow_icon)
                )
            }

        }

    }
}

@SuppressLint("DefaultLocale")
@Composable
fun TimeCounter(modifier: Modifier = Modifier, currentTime: String, prayTime: PrayTimes) {
    var isClicked by remember { mutableStateOf(false) }
    val rotationY by animateFloatAsState(
        targetValue = if (isClicked) 180f else 0f,
        animationSpec = tween(1000, easing = EaseInOutQuad),
        label = ""
    )
    val scale by animateFloatAsState(
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

    val prayTimeList = prayTime.toTimeList()
    val currentSeconds = currentTime.convertTimeToSeconds()

    val nextTimeIndex = prayTimeList.indexOfFirst { it.convertTimeToSeconds() > currentSeconds }.takeIf { it != -1 } ?: 0
    val nextTime = prayTimeList[nextTimeIndex]
    val previousTime = if (nextTimeIndex == 0) prayTimeList.last() else prayTimeList[nextTimeIndex - 1]

    val totalSeconds = (nextTime.convertTimeToSeconds() - previousTime.convertTimeToSeconds()).let {
        if (it < 0) it + 24 * 3600 else it
    }

    val elapsedSeconds = (currentSeconds - previousTime.convertTimeToSeconds()).let {
        if (it < 0) it + 24 * 3600 else it
    }

    val remainingSeconds = totalSeconds - elapsedSeconds
    val sweepAngle = (remainingSeconds.toFloat() / totalSeconds.toFloat()) * 360f

    val formattedTime = String.format("%02d:%02d:%02d", remainingSeconds / 3600, (remainingSeconds / 60) % 60, remainingSeconds % 60)

    Box(
        modifier = modifier
            .noRippleClickable { isClicked = !isClicked }
            .graphicsLayer {
                this.rotationY = rotationY
                this.scaleY = scale
                this.scaleX = scale
            }
            .padding(top = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            modifier = Modifier.graphicsLayer { this.rotationY = rotationY },
            text = formattedTime,
            fontSize = 18.sp
        )

        TimeCounterCanvas(sweepAngle)
    }
}

@Composable
fun TimeCounterCanvas(sweepAngle : Float) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondary
    val bgColor = MaterialTheme.colorScheme.background
    Canvas(modifier = Modifier.fillMaxSize()) {
        val center = Offset(size.width / 2f, size.height / 2f)
        val radius = size.minDimension / 2f
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

@Composable
fun PrayerMethodSelector(
    methods: Map<String, Int>,
    onMethodSelected: (String) -> Unit
) {
    val expanded = remember { mutableStateOf(false) }
    val selectedMethod = remember { mutableStateOf(methods.keys.first()) }

    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)) {
        Text(
            text = stringResource(R.string.select_prayer_calculation_method),
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
@Composable
fun FancyDuaDialog(
    duaDetail: DuaCategoryDetail,
    onDismiss: () -> Unit
) {

    val infiniteTransition = rememberInfiniteTransition(label = "infiniteTransition")

    val gradientColors = listOf(
        infiniteTransition.animateColor(
            initialValue = Color(0xFF9C27B0),
            targetValue = Color(0xFF673AB7),
            animationSpec = infiniteRepeatable(
                animation = tween(3000),
                repeatMode = RepeatMode.Reverse
            ),
            label = "colorAnimation1"
        ).value,
        infiniteTransition.animateColor(
            initialValue = Color(0xFF3F51B5),
            targetValue = Color(0xFF2196F3),
            animationSpec = infiniteRepeatable(
                animation = tween(3000),
                repeatMode = RepeatMode.Reverse
            ),
            label = "colorAnimation2"
        ).value
    )

    Dialog(
        onDismissRequest = onDismiss
    ) {
        val scrollState = rememberScrollState()
        Box(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .clip(RoundedCornerShape(20.dp))
                .background(
                    brush = Brush.linearGradient(colors = gradientColors)
                )
                .padding(4.dp)
                .verticalScroll(scrollState)
        ) {

            Column(
                modifier = Modifier
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Filled.Favorite,
                    contentDescription = stringResource(R.string.prayer),
                    modifier = Modifier
                        .size(60.dp)
                        .padding(bottom = 16.dp),
                    tint = Color.White.copy(alpha = 0.9f)
                )

                val localizedTitle = getLocalizedString(
                    englishText = duaDetail.title,
                    turkishText = duaDetail.titleTr
                )

                Text(
                    text = localizedTitle,
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 20.dp)
                )

                HorizontalDivider(
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .padding(bottom = 20.dp),
                    thickness = 1.dp,
                    color = Color.White.copy(alpha = 0.7f)
                )

                Text(
                    text = duaDetail.arabic,
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White.copy(alpha = 0.9f),
                    textAlign = TextAlign.Center,
                    fontStyle = FontStyle.Italic,
                    modifier = Modifier.padding(bottom = 16.dp)
                )


                Text(
                    text = duaDetail.latin,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 20.dp)
                )

                Text(
                    text = getLocalizedString(duaDetail.translation,duaDetail.translationTr),
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 20.dp)
                )

                Text(
                    text = stringResource(R.string.source, duaDetail.source),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.8f),
                    textAlign = TextAlign.End,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                )

                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .padding(top = 8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White.copy(alpha = 0.2f)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = stringResource(R.string.amen),
                        color = Color.White,
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun DuaCategoryCardCompose(duaCategoryData: DuaCategoryData,onDuaCategoryClick: (Int) -> Unit){
    Card (
        modifier = Modifier
            .padding(10.dp),
        onClick = {
            onDuaCategoryClick(duaCategoryData.id)
        },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
        elevation = CardDefaults.cardElevation(10.dp),
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(
            modifier = Modifier.padding(top = 10.dp, bottom = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                modifier = Modifier
                    .padding(start = 7.dp)
                    .size(24.dp),
                painter = rememberAsyncImagePainter(R.drawable.pray),
                contentDescription = stringResource(R.string.face_icon),
            )
            Text(
                modifier = Modifier
                    .padding(vertical = 7.dp, horizontal = 7.dp)
                    .basicMarquee(iterations = Int.MAX_VALUE),
                text = getLocalizedString(duaCategoryData.name,duaCategoryData.nameTr),
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1,
                softWrap = false,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.W600
            )
        }
    }

}

private fun updatePrayAlarmsOrCheckPermission(isNotificationPermissionGranted : Boolean,prayerAlarm : PrayerAlarm,index: Int, mainScreenViewModel: MainScreenViewModel,permissionLauncher: ActivityResultLauncher<String>){
    if (isNotificationPermissionGranted) {
        if (prayerAlarm.isEnabled) {
            mainScreenViewModel.updateGlobalAlarm(
                prayerAlarm.alarmType,
                0L,
                "16-01-2025 00:00",
                false,
                0L
            )
            return
        } else {
            mainScreenViewModel.updateGlobalAlarm(
                prayerAlarm.alarmType,
                mainScreenViewModel.getAlarmTime(index).first,
                mainScreenViewModel.getAlarmTime(index).second,
                true,
                0L
            )
        }
    } else {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }
}