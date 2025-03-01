package com.fatih.prayertime.presentation.util_screen.view

import android.content.res.Resources
import android.util.Log
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandIn
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.with
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.fatih.prayertime.util.Constants.screens
import com.fatih.prayertime.util.ScreenData

@Composable
fun UtilitiesScreen(bottomPaddingValues : Dp,navController: NavController) {
    Column  (
        modifier = Modifier.fillMaxSize(1f).padding(bottom = bottomPaddingValues),
    ) {
        val utilityScreens = screens.drop(4)
        UtilitiesRow(utilityScreens[0], utilityScreens[1], navController )
        UtilitiesRow(utilityScreens[2], utilityScreens[3],navController)
        UtilitiesRow(utilityScreens[4], utilityScreens[5],navController)
    }
}

@Composable
fun ColumnScope.UtilitiesRow(firstCard : ScreenData, secondCard : ScreenData,navController: NavController) {
    Row (
        modifier = Modifier.weight(1f),
        horizontalArrangement = Arrangement.Center,
    ){
        UtilitiesCardData(firstCard,navController)
        UtilitiesCardData(secondCard,navController)
    }
}

@Composable
fun RowScope.UtilitiesCardData(screenData: ScreenData,navController: NavController) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .weight(1f)
            .fillMaxSize(1f)
        ,
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.elevatedCardElevation(15.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        onClick = {
            navController.navigate(screenData.route)
        }
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(16.dp).fillMaxSize(1f)
        ) {
            Image(
                painter = painterResource(id = screenData.iconRoute),
                contentDescription = screenData.title,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = screenData.title,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                textAlign = TextAlign.Center
            )
        }
    }
}