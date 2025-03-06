package com.fatih.prayertime.presentation.dua_categories_screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColor
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateValue
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.fatih.prayertime.data.remote.dto.duadto.DuaCategories
import com.fatih.prayertime.data.remote.dto.duadto.DuaCategoryData
import com.fatih.prayertime.util.Constants.colors
import com.fatih.prayertime.util.Constants.screens
import com.fatih.prayertime.util.ErrorView
import com.fatih.prayertime.util.LoadingView
import com.fatih.prayertime.util.Status
import com.fatih.prayertime.util.TitleView
import com.fatih.prayertime.util.navigateToScreen
import kotlinx.coroutines.delay
import kotlin.random.Random

@Composable
fun DuaCategoriesScreen(bottomPaddingValues: Dp, navController: NavController,duaCategoriesViewModel: DuaCategoriesViewModel ) {
    val duaCategories by duaCategoriesViewModel.duaCategories.collectAsState()
    Box(modifier = Modifier.fillMaxSize(1f), contentAlignment = Alignment.Center){

        when(duaCategories.status) {
            Status.SUCCESS -> {
                DuaCategoriesGridView(duaCategories.data!!, navController,duaCategoriesViewModel )
            }
            Status.LOADING ->{
                LoadingView()
            }
            Status.ERROR ->{
                ErrorView(message = duaCategories.message.toString()){
                    duaCategoriesViewModel.getDuaCategories()
                }
            }
        }
    }
    TitleView("Dua Topics")
}

@Composable
fun DuaCategoriesGridView(duaCategories: DuaCategories, navController: NavController,duaCategoriesViewModel: DuaCategoriesViewModel) {
    LazyVerticalStaggeredGrid (
        modifier = Modifier.padding(start = 15.dp, end = 15.dp),
        columns = StaggeredGridCells.Fixed(2),
    )  {
        items(duaCategories.data) { duaCategoryData ->
            DuaCategoryCard(duaCategoryData, navController, duaCategoriesViewModel)
        }
    }
}

@Composable
fun DuaCategoryCard(duaCategoryData :DuaCategoryData, navController: NavController,duaCategoriesViewModel: DuaCategoriesViewModel) {
    val infiniteTransition = rememberInfiniteTransition()
    val randomColor = remember { colors.random() }
    val targetColor = remember { colors.filter { it != randomColor }.random() }
    val translation = infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = if (Random.nextBoolean()) Random.nextFloat() * 10f + 1f else Random.nextFloat() * -4f - 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000),
            repeatMode = RepeatMode.Reverse
        )
    )

    val animatedColor = infiniteTransition.animateColor(
        initialValue = randomColor,
        targetValue = targetColor,
        animationSpec = infiniteRepeatable(
            animation = tween(10000),
            repeatMode = RepeatMode.Reverse
        )
    )
    val scale = infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(tween(3000),repeatMode = RepeatMode.Reverse),
    )
    Card(
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        modifier = Modifier
            .padding(bottom = 20.dp, top = 20.dp, start = 10.dp, end = 10.dp)
            .graphicsLayer {
                scaleY = scale.value
                translationX = translation.value.dp.toPx()
                translationY = translation.value.dp.toPx()
                rotationZ = translation.value / 2f
            }

            ,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
        onClick = {
            duaCategoriesViewModel.updateDetailPath(duaCategoryData.slug)
            navController.navigateToScreen(screens[4])
        }
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .animateContentSize(tween(1000))

        ) {
            Text(
                textAlign = TextAlign.Center,
                text = duaCategoryData.name,
                style = MaterialTheme.typography.titleMedium,
                color = animatedColor.value,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                textAlign = TextAlign.Center,
                text = "Number of prayers in this category :  ${duaCategoryData.total}",
                style = MaterialTheme.typography.titleSmall,
                color = animatedColor.value,
                modifier = Modifier.fillMaxWidth()
            )

        }
    }
}