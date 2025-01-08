package com.fatih.namazvakitleri.presentation.main_screen.view

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.EaseInOutQuad
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.graphics.ExperimentalAnimationGraphicsApi
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.with
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.border
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
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.exyte.animatednavbar.utils.noRippleClickable
import com.fatih.namazvakitleri.R
import com.fatih.namazvakitleri.domain.model.PrayTimes
import com.fatih.namazvakitleri.presentation.main_activity.viewmodel.PermissionViewModel
import com.fatih.namazvakitleri.presentation.main_screen.viewmodel.MainScreenViewModel
import com.fatih.namazvakitleri.presentation.ui.theme.IconBackGroundColor
import com.fatih.namazvakitleri.presentation.ui.theme.IconColor
import com.fatih.namazvakitleri.presentation.ui.theme.LightGreen
import com.fatih.namazvakitleri.presentation.ui.theme.MyGreen
import com.fatih.namazvakitleri.util.toPrayList
import kotlinx.coroutines.delay
import java.time.LocalDate
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun MainScreen(permissionViewModel: PermissionViewModel) {
    val mainScreenViewModel : MainScreenViewModel = hiltViewModel()
    val scrollState = rememberScrollState()
    GetLocationInformation(mainScreenViewModel,permissionViewModel)
    Column(modifier = Modifier.verticalScroll(scrollState, enabled = true) ){
        TopBarCompose()
        PrayScheduleCompose()
        PrayNotificationCompose()
        DailyPrayCompose()
    }
}

@Composable
fun GetLocationInformation(mainScreenViewModel: MainScreenViewModel,permissionViewModel: PermissionViewModel){
    val permissionGranted by permissionViewModel.permissionGranted.collectAsState()

    if (permissionGranted){
        LaunchedEffect (Unit) {
            mainScreenViewModel.getCurrentAddress()
        }
    }else{
        LaunchedEffect(Unit) {
            mainScreenViewModel.getCurrentAddressFromDatabase()
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
                                    .basicMarquee()
                                    .padding(vertical = 7.dp, horizontal = 7.dp),
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

@Composable
fun PrayNotificationCompose() {
    Card(
        modifier = Modifier.padding(top = 20.dp),
        onClick = {},
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(10.dp),
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start) {
            Row(
                modifier = Modifier
                    .padding(10.dp)
                    .fillMaxWidth(1f),
                verticalAlignment = Alignment.CenterVertically)
            {
                Icon(
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

                Icon(
                    painter = painterResource(R.drawable.save_icon),
                    contentDescription = "Save Icon"
                )
            }
            Spacer(modifier = Modifier.height(5.dp))
            Row(
                modifier = Modifier
                    .padding(horizontal = 10.dp)
                    .background(IconBackGroundColor.copy(alpha = 0.65f), RoundedCornerShape(10.dp))
                    .fillMaxWidth(1f)
                ,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val prayTimes = listOf("Morning","Noon","Afternoon","Evening","Night")
                prayTimes.forEach { prayTime ->
                    Column (
                        modifier = Modifier
                            .weight(1f)
                            .padding(vertical = 10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ){
                        Icon(
                            painter = painterResource(R.drawable.check_circle),
                            contentDescription = "Check Circle",
                            tint = IconColor
                        )
                        Text(
                            text = prayTime,
                            style = MaterialTheme.typography.titleSmall,
                            color = LocalContentColor.current.copy(alpha = 0.87f),
                            maxLines = 1,
                            softWrap = false,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.W600
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                modifier = Modifier
                    .padding(start = 10.dp, end = 10.dp, bottom = 15.dp)
                    .background(Color.Transparent)
                    .fillMaxWidth(1f)
                    .border(1.dp, IconBackGroundColor, RoundedCornerShape(10.dp))
                    .padding(vertical = 5.dp)
                ,
                text = "Prayer Together",
                style = MaterialTheme.typography.titleLarge,
                fontSize = 18.sp ,
                color = IconColor,
                maxLines = 1,
                softWrap = false,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.W500
            )
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
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
                Column(
                    modifier = Modifier.padding(start = 10.dp, bottom = 10.dp, end = 10.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    val formattedDate by mainScreenViewModel.formattedDate.collectAsState()
                    val formattedTime by mainScreenViewModel.formattedTime.collectAsState()
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
                        text = "Maghrib is less than 05:25",
                        modifier = Modifier.padding(start = 3.dp, top = 1.dp),
                        style = MaterialTheme.typography.labelLarge,
                        color = LocalContentColor.current.copy(alpha = 0.6f),
                        maxLines = 1,
                        softWrap = false,
                        textAlign = TextAlign.Center
                    )
                }

                TimeCounter(Modifier.weight(1f).size(75.dp),60)
            }
            HorizontalDivider(Modifier.padding(15.dp))
            val prayList = PrayTimes(
                Pair("Morning","06.15"),
                Pair("Noon","12.00"),
                Pair("Afternoon","18.00"),
                Pair("Evening","19.00"),
                Pair("Night","20.00")).toPrayList()

            Row(
                modifier = Modifier
                    .fillMaxWidth(1f)
                    .padding(top = 10.dp)
            ) {
                prayList.forEach { prayPair ->
                    PrayTimesRow(prayPair)
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
fun RowScope.PrayTimesRow(prayPair: Triple<String, String, ImageVector>) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.weight(1f)
    ) {
        Text(
            text = "Morning",
            style = MaterialTheme.typography.bodySmall,
            maxLines = 1,
            softWrap = false,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.W600,
        )
        Icon(modifier = Modifier.padding(top = 5.dp), imageVector = prayPair.third,contentDescription = prayPair.first)
        Text(
            modifier = Modifier.padding(top = 5.dp, bottom = 8.dp),
            text = "03:53",
            style = MaterialTheme.typography.bodySmall,
            maxLines = 1,
            softWrap = false,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.W600,
        )
    }
}

@Composable
fun TopBarCompose() {
    AddressBar()
    PrayerBar()
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
                val currentAddress by mainScreenViewModel.currentAddress.collectAsState()
                val text by remember {
                    derivedStateOf {
                        when(currentAddress.data){
                            null -> "Location"
                            else -> if (!isExpanded) {
                                currentAddress.data!!.city + ", " + currentAddress.data!!.country
                            }else{
                                currentAddress.data!!.fullAddress?:""
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

@Composable
fun TimeCounter(modifier: Modifier = Modifier,counter: Int) {
    var isClicked by remember { mutableStateOf(false)  }
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

    Box(modifier = modifier.noRippleClickable {
        isClicked = !isClicked
    }.graphicsLayer {
        this.rotationY = rotationY.value
        this.scaleY = scale.value
        this.scaleX = scale.value
    }, contentAlignment = Alignment.Center){
        var progress by remember { mutableIntStateOf(counter) }
        LaunchedEffect(Unit) {
            while (true) {
                delay(1000)
                progress--
                if (progress < 0) progress = 60
            }
        }
        Text(modifier = Modifier.graphicsLayer {
            this.rotationY = rotationY.value
        }, text = progress.toString(), fontSize = 22.sp)
        Canvas(modifier = modifier) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val radius = size.width / 2f
            val strokeWidth = 15f
            val circleRadius = 17f

            drawCircle(
                color = IconBackGroundColor,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )

            val sweepAngle = -(progress * 360f) / 60f
            drawArc(
                brush = Brush.linearGradient(
                    colors = listOf(IconColor, LightGreen),
                    tileMode = TileMode.Repeated
                ),
                startAngle = -90f,
                sweepAngle = -sweepAngle,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
            val angleRadians = (-90f - sweepAngle) * (PI / 180f).toFloat()
            val circleCenterX = center.x + radius * cos(angleRadians)
            val circleCenterY = center.y + radius * sin(angleRadians)

            drawCircle(
                brush =  Brush.linearGradient(
                    colors = listOf(IconColor, LightGreen),
                    tileMode = TileMode.Repeated
                ),
                radius = circleRadius,
                center = Offset(circleCenterX, circleCenterY)
            )
        }
    }



}
