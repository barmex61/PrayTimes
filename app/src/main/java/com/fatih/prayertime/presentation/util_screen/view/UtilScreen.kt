package com.fatih.prayertime.presentation.util_screen.view


import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding

import androidx.compose.foundation.shape.RoundedCornerShape
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
        val utilityScreens = screens.drop(4) + screens[1]
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