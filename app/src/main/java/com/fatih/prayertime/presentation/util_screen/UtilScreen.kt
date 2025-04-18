package com.fatih.prayertime.presentation.util_screen


import androidx.compose.animation.animateColor
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.graphicsLayer

import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.fatih.prayertime.domain.model.ScreenData
import com.fatih.prayertime.util.composables.FullScreenLottieAnimation
import com.fatih.prayertime.util.composables.LottieAnimationSized
import com.fatih.prayertime.util.config.NavigationConfig.screens
import com.fatih.prayertime.util.extensions.navigateToScreen
import com.fatih.prayertime.util.composables.TitleView
import com.fatih.prayertime.util.model.enums.PrayTimesString
import kotlin.math.absoluteValue
@Composable
fun UtilitiesScreen(modifier: Modifier, navController: NavController) {
    LottieAnimationSized (
        lottieFile = "dua_moon_anim.lottie",
        autoPlay = true,
        loop = true,
        width = 250,
        height = 250,
        speed = 1.5f,
        lottieAnimDuration = 1000
    ) {
        val utilityScreens = remember {
            screens.takeLast(7) + screens[1]
        }

        LazyColumn(
            modifier = modifier.fillMaxSize()
        ) {
            items(utilityScreens.chunked(2)) { rowItems ->
                UtilitiesRow(rowItems, navController)
            }
        }
    }
    TitleView("Utilities")
}

@Composable
fun UtilitiesRow(rowItems: List<ScreenData>, navController: NavController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        rowItems.forEach { screenData ->
            UtilitiesCard(screenData, navController)
        }
    }
}

@Composable
fun RowScope.UtilitiesCard(screenData: ScreenData, navController: NavController) {
    val infiniteTransition = rememberInfiniteTransition()
    val animatedColor by infiniteTransition.animateColor(
        initialValue = MaterialTheme.colorScheme.onPrimaryContainer,
        targetValue = MaterialTheme.colorScheme.primary,
        animationSpec = infiniteRepeatable(
            animation = tween(3000),
            repeatMode = RepeatMode.Reverse
        )
    )
    val animationProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000),
            repeatMode = RepeatMode.Reverse
        )
    )

    Card(
        modifier = Modifier
            .padding(8.dp)
            .weight(1f)
            .wrapContentHeight()
            .graphicsLayer {
                translationY = animationProgress * 20 - 10
                translationX = animationProgress * 20 - 10
                rotationY = animationProgress * -2 + 1f
                rotationZ = animationProgress * 2 - 1f
            },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.elevatedCardElevation(15.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        onClick = { navController.navigateToScreen(screenData.route) }
    ) {
        UtilitiesCardContent(screenData, animationProgress, animatedColor)
    }
}

@Composable
fun UtilitiesCardContent(screenData: ScreenData, animationProgress: Float, animatedColor: Color) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Image(
            painter = rememberAsyncImagePainter(screenData.painterRoute ?: screenData.iconRoute),
            contentDescription = screenData.title.name,
            colorFilter = ColorFilter.colorMatrix(
                ColorMatrix().apply { setToSaturation(animationProgress) }
            ),
            modifier = Modifier
                .graphicsLayer {
                    rotationY = animationProgress * 60 - 30
                    scaleY = (rotationY / 30).absoluteValue.coerceAtLeast(0.90f)
                }
                .size(130.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(screenData.title.stringResId),
            style = MaterialTheme.typography.titleLarge,
            color = animatedColor,
            textAlign = TextAlign.Center,
        )
    }
}
