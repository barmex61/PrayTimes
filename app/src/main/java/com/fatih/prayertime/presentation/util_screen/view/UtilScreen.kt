package com.fatih.prayertime.presentation.util_screen.view


import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateColor
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.with
import androidx.compose.foundation.Image
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridItemScope
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.graphicsLayer

import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import androidx.navigation.NavController
import com.fatih.prayertime.util.Constants.screens
import com.fatih.prayertime.util.ScreenData
import kotlin.math.absoluteValue

@SuppressLint("MutableCollectionMutableState")
@Composable
fun UtilitiesScreen(bottomPaddingValues : Dp,navController: NavController) {
    val utilityScreens = screens.drop(4) + screens[1]

    Column(
        modifier = Modifier.animateContentSize().padding(bottom = bottomPaddingValues),

     ) {
        UtilitiesRow(firstCard = utilityScreens[0], secondCard = utilityScreens[1],navController)
        UtilitiesRow(firstCard = utilityScreens[2], secondCard = utilityScreens[3],navController)
        UtilitiesRow(firstCard = utilityScreens[4], secondCard = utilityScreens[5],navController)
    }
}

@Composable
fun ColumnScope.UtilitiesRow(firstCard : ScreenData, secondCard : ScreenData,navController: NavController) {
    Row (
        modifier = Modifier.weight(1f),
        horizontalArrangement = Arrangement.Center,
    ){
        UtilitiesCardData(firstCard, navController)
        UtilitiesCardData(secondCard,navController)
    }
}

@Composable
fun RowScope.UtilitiesCardData(screenData: ScreenData,navController: NavController) {
    val infiniteTransition = rememberInfiniteTransition()
    val animatedColor = infiniteTransition.animateColor(
        initialValue = MaterialTheme.colorScheme.onPrimaryContainer,
        targetValue = MaterialTheme.colorScheme.primary,
        animationSpec = infiniteRepeatable(
            animation = tween(3000),
            repeatMode = RepeatMode.Reverse
        )
    )
    val animationProgress = infiniteTransition.animateFloat(
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
            .graphicsLayer {
                translationY = animationProgress.value * 20 - 10
                translationX = animationProgress.value * 20 - 10
                rotationY = animationProgress.value * -2 + 1f
                rotationZ = animationProgress.value * 2 - 1f
            }
        ,
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.elevatedCardElevation(15.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        onClick = {
            navController.navigate(screenData.route) {
                popUpTo(navController.graph.startDestinationId) {
                    saveState = false
                }
                launchSingleTop = true
                restoreState = false
                if (screenData.route == "qibla" ) navController.popBackStack()
            }
        }
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(16.dp).fillMaxSize(1f)
        ) {

            Image(
                painter = painterResource(id = screenData.painterRoute ?: screenData.iconRoute),
                contentDescription = screenData.title,
                colorFilter = ColorFilter.colorMatrix(
                    ColorMatrix().apply {
                        setToSaturation(animationProgress.value)
                    }
                ),
                modifier = Modifier.graphicsLayer{
                    rotationY = animationProgress.value * 60 - 30
                    scaleY = (rotationY / 30).absoluteValue.coerceAtLeast(0.90f)
                }
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = screenData.title,
                style = MaterialTheme.typography.titleLarge,
                color = animatedColor.value,
                textAlign = TextAlign.Center,
            )
        }
    }
}