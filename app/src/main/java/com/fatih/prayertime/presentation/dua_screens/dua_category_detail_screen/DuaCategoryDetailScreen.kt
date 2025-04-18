package com.fatih.prayertime.presentation.dua_screens.dua_category_detail_screen

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.InfiniteTransition
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*

import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer

import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController

import com.fatih.prayertime.data.remote.dto.duadto.DuaCategoryDetail
import com.fatih.prayertime.util.utils.getLocalizedString
import com.fatih.prayertime.util.composables.ErrorView
import com.fatih.prayertime.util.config.NavigationConfig.screens
import com.fatih.prayertime.util.config.ThemeConfig.colors
import com.fatih.prayertime.util.extensions.navigateToScreen
import com.fatih.prayertime.util.composables.TitleView

import kotlin.random.Random

@Composable
fun DuaCategoryDetailScreen(
    modifier: Modifier,
    navController: NavController,
    categoryId : Int,
    duaCategoryDetailViewModel: DuaCategoryDetailScreenViewModel = hiltViewModel(),

) {
    val infiniteTransition = rememberInfiniteTransition()
    val duaCategoryDetailList by duaCategoryDetailViewModel.duaCategoryDetailList.collectAsState()
    val duaCategoryId by duaCategoryDetailViewModel.duaCategoryId.collectAsState()

    LaunchedEffect(key1 = categoryId) {
        duaCategoryDetailViewModel.updateDuaCategoryId(categoryId)
    }

    if (duaCategoryDetailList == null){
        ErrorView("Error occurred") {
            duaCategoryDetailViewModel.retryDuaCategoryDetailLoading()
        }
    }else{
        Box(modifier = Modifier.fillMaxSize(1f), contentAlignment = Alignment.Center){
            LazyVerticalStaggeredGrid(
                modifier = modifier,
                columns = StaggeredGridCells.Fixed(2)
            ) {

                items(duaCategoryDetailList!!){ duaCategoryDetail ->
                    DuaCategoryDetailCard(duaCategoryDetail,infiniteTransition ){
                        val subRoute = screens[7].route.replace("{duaId}","${duaCategoryDetail.id}")
                        val route = subRoute.replace("{categoryId}","$duaCategoryId")
                        navController.navigateToScreen(route)
                    }
                }

            }
        }
    }
    TitleView("Dua Details")

}

@Composable
fun DuaCategoryDetailCard(
    duaCategoryDetail: DuaCategoryDetail,
    infiniteTransition: InfiniteTransition,
    onNavigate : () -> Unit) {

    val randomColor = remember { colors.random() }
    val targetColor = remember { colors.filter { it != randomColor }.random() }
    val translation = infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = if (Random.nextBoolean()) Random.nextFloat() * 2f + 1f else Random.nextFloat() * -2f - 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000),
            repeatMode = RepeatMode.Reverse
        )
    )

    val animatedColor = infiniteTransition.animateColor(
        initialValue = randomColor,
        targetValue = targetColor,
        animationSpec = infiniteRepeatable(
            animation = tween(3000),
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
            .padding(start = 10.dp, end = 10.dp, bottom = 15.dp, top = 15.dp)
            .graphicsLayer {
                scaleY = scale.value
                translationX = translation.value.dp.toPx()
                translationY = translation.value.dp.toPx()
                rotationZ = translation.value / 2f
            }
        ,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
        onClick = onNavigate
    ) {

        Text(
            textAlign = TextAlign.Center,
            text = getLocalizedString(duaCategoryDetail.title,duaCategoryDetail.titleTr),
            style = MaterialTheme.typography.titleMedium,
            color = animatedColor.value,
            modifier = Modifier.fillMaxWidth().padding(10.dp)
        )
    }

}
