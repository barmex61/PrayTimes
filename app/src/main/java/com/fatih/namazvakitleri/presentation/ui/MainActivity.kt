package com.fatih.namazvakitleri.presentation.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.outlined.Face
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.fatih.namazvakitleri.domain.model.PrayTimes
import com.fatih.namazvakitleri.presentation.ui.theme.IconBackGroundColor
import com.fatih.namazvakitleri.presentation.ui.theme.IconColor
import com.fatih.namazvakitleri.presentation.ui.theme.NamazVakitleriTheme
import com.fatih.namazvakitleri.util.LatLong
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val position = LatLong(40.9534728,39.9312456)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NamazVakitleriTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    println(innerPadding)
                    Box(
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.background)
                            .padding(
                                15.dp,
                                innerPadding.calculateTopPadding() + 10.dp,
                                15.dp,
                                innerPadding.calculateBottomPadding()
                            ),

                    ) {
                        Column {
                            TopBarItems()
                            PrayScheduleColumn(Modifier.padding(top = 30.dp).background(Color.White, RoundedCornerShape(10.dp)))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PrayScheduleColumn(modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Row (horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically){
            Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.Center) {
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
                    color = LocalContentColor.current.copy(alpha = 0.5f),
                    maxLines = 1,
                    softWrap = false,
                    textAlign = TextAlign.Center
                )
            }
            Image(
                modifier = Modifier.weight(1f).height(70.dp),
                imageVector = Icons.Outlined.Face,
                contentDescription = "Remain Time")
        }
        HorizontalDivider(Modifier.padding(15.dp))
        val prayTimes = PrayTimes(
                Pair("Morning","06.15"),
                Pair("Sunrise","07.00"),
                Pair("Noon","12.00"),
                Pair("Afternoon","18.00"),
                Pair("Evening","19.00"),
                Pair("Night","20.00"))

        Row {

        }
    }
}

@Composable
fun TopBarItems() {
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
            .padding(top = 20.dp),
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
            modifier = cardModifier.clip(RoundedCornerShape(10.dp)).clickable {  },
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(10.dp),
            border = BorderStroke(1.dp, IconColor.copy(alpha = 0.15f)),
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


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    NamazVakitleriTheme {
        Box(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .padding(15.dp, 20.dp, 15.dp, 20.dp),

            ) {
            Column {
                TopBarItems()
                PrayScheduleColumn(Modifier.fillMaxWidth().padding(top = 30.dp).background(Color.White, RoundedCornerShape(10.dp)))
            }
        }
    }
}