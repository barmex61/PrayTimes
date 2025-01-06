package com.fatih.namazvakitleri.presentation.ui

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.outlined.Face
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemColors
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.BlurEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.OffsetEffect
import androidx.compose.ui.graphics.RenderEffect
import androidx.compose.ui.graphics.Shader
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.exyte.animatednavbar.AnimatedNavigationBar
import com.exyte.animatednavbar.utils.noRippleClickable
import com.fatih.namazvakitleri.R
import com.fatih.namazvakitleri.domain.model.PrayTimes
import com.fatih.namazvakitleri.presentation.ui.theme.BackGround
import com.fatih.namazvakitleri.presentation.ui.theme.IconBackGroundColor
import com.fatih.namazvakitleri.presentation.ui.theme.IconColor
import com.fatih.namazvakitleri.presentation.ui.theme.NamazVakitleriTheme
import com.fatih.namazvakitleri.util.Constants.bottomNavItems
import com.fatih.namazvakitleri.util.LatLong
import com.fatih.namazvakitleri.util.toPrayList
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
                    modifier = Modifier.fillMaxSize().graphicsLayer {

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

@Composable
fun BottomBarCompose() {

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
                Icon(painter = painterResource(R.drawable.save_icon), contentDescription = "Save Icon")
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

@SuppressLint("NewApi")
@Composable
fun PrayScheduleCompose() {
    Card(
        modifier = Modifier.padding(top = 20.dp),
        onClick = {},
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(10.dp),
        shape = RoundedCornerShape(10.dp)
    ) {
        Column {
            Row (
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ){
                Column(
                    modifier = Modifier.padding(10.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        modifier = Modifier.padding(start = 3.dp, top = 1.dp),
                        text = "28 Mayis 2025",
                        style = MaterialTheme.typography.labelLarge,
                        color = LocalContentColor.current.copy(alpha = 0.87f),
                        maxLines = 1,
                        softWrap = false,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        modifier = Modifier.padding(start = 3.dp, top = 1.dp),
                        text = "17:21",
                        style = MaterialTheme.typography.headlineLarge,
                        color = LocalContentColor.current.copy(alpha = 0.87f),
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        softWrap = false,
                        textAlign = TextAlign.Center
                    )
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
                var isPerspective by remember { mutableStateOf(false) }
                val rotationY by animateFloatAsState(
                    targetValue = if (isPerspective) 180f else 0f,
                    animationSpec = tween(durationMillis = 1000)
                )

                Image(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .weight(1f)
                        .height(70.dp)
                        .noRippleClickable {
                            isPerspective = !isPerspective
                        }
                        .graphicsLayer {
                            this.rotationY = rotationY
                            this.rotationZ = rotationY
                        },
                    imageVector = Icons.Outlined.Face,
                    contentDescription = "Remain Time")
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

@Composable
fun RowScope.PrayTimesRow(prayPair: Triple<String, String,ImageVector>) {
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
    RowWithIcons(
        boxModifier = Modifier.fillMaxWidth(),
        rowModifier = Modifier.padding(start = 7.dp, end = 7.dp, top = 3.dp, bottom = 3.dp),
        text = "Turkey/Trabzon",
        leadingIcon = Icons.Outlined.LocationOn,
        endIcon = Icons.Outlined.Notifications,
        endIconBackgroundEnabled = false
    )
    RowWithIcons(
        boxModifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp),
        cardModifier = Modifier.fillMaxWidth(),
        rowModifier = Modifier.padding(start = 15.dp, end = 15.dp, top = 10.dp, bottom = 10.dp),
        text = "Start your day with these prayers",
        leadingIcon = Icons.Outlined.Favorite,
        endIcon = Icons.AutoMirrored.Outlined.KeyboardArrowRight,
        endIconBackgroundEnabled = true,
        endIconPaddingEndPadding = 10.dp
    )
}

@Composable
fun RowWithIcons(
    boxModifier: Modifier,
    cardModifier: Modifier = Modifier,
    rowModifier: Modifier,
    text: String,
    leadingIcon: ImageVector,
    endIcon: ImageVector,
    endIconBackgroundEnabled: Boolean = false,
    endIconPaddingEndPadding: Dp = 0.dp,
) {

    Box (modifier = boxModifier, contentAlignment = Alignment.CenterStart) {
        Card (
            modifier = cardModifier,
            onClick = {},
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(10.dp),
            shape = RoundedCornerShape(10.dp)
        )
        {
            Row(
                modifier = rowModifier
            ) {
                Icon(
                    imageVector = leadingIcon,
                    contentDescription = "Location Icon",
                    tint = IconColor
                )
                Text(
                    modifier = Modifier.padding(start = 3.dp, top = 1.dp),
                    text = text,
                    style = MaterialTheme.typography.labelLarge,
                    color = LocalContentColor.current.copy(alpha = 0.87f),
                    maxLines = 1,
                    softWrap = false,
                    textAlign = TextAlign.Center
                )
            }
        }
        Icon(
            modifier = Modifier
                .padding(end = endIconPaddingEndPadding)
                .background(
                    shape = RoundedCornerShape(15.dp),
                    color = if (endIconBackgroundEnabled) IconBackGroundColor else Color.Transparent
                )
                .align(Alignment.CenterEnd)
                .size(25.dp),
            imageVector = endIcon,
            contentDescription = "Notification Icon"
        )
    }
}
