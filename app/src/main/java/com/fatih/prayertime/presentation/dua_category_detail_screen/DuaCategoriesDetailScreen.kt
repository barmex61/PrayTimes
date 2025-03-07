
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.InfiniteTransition
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*

import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer

import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

import com.fatih.prayertime.data.remote.dto.duadto.DuaCategoryDetailData
import com.fatih.prayertime.presentation.dua_categories_screen.DuaCategoriesViewModel
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
fun DuaCategoryDetailScreen(
    bottomPaddingValues: Dp,
    duaCategoriesViewModel: DuaCategoriesViewModel,
    navController: NavController
) {
    val infiniteTransition = rememberInfiniteTransition()
    val duaCategoryDetail by duaCategoriesViewModel.duaCategoryDetail.collectAsState()

    when(duaCategoryDetail.status){
        Status.SUCCESS->{
            Box(modifier = Modifier.fillMaxSize(1f), contentAlignment = Alignment.Center){
                LazyVerticalStaggeredGrid(
                    modifier = Modifier.padding(bottom = bottomPaddingValues),
                    columns = StaggeredGridCells.Fixed(2)
                ) {
                    duaCategoryDetail.data?.let { duaCategoryDetailData ->
                        items(duaCategoryDetailData.data){ duaCategoryDetail ->
                            DuaCategoryDetailCard(duaCategoryDetail,infiniteTransition,duaCategoriesViewModel, navController)
                        }
                    }
                }
            }
        }
        Status.LOADING->{
            LoadingView()
        }
        Status.ERROR->{
            ErrorView(duaCategoryDetail.message?:"Error occurred"){
                duaCategoriesViewModel.getDuaCategoryDetail()
            }
        }
    }

    TitleView("Dua Details")

}

@Composable
fun DuaCategoryDetailCard(
    duaCategoryDetailData: DuaCategoryDetailData,
    infiniteTransition: InfiniteTransition,
    duaCategoriesViewModel: DuaCategoriesViewModel,
    navController: NavController) {

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
        onClick = {
            duaCategoriesViewModel.updateDuaId(duaCategoryDetailData.id)
            navController.navigateToScreen(screens[7])
        }
    ) {

        Text(
            textAlign = TextAlign.Center,
            text = duaCategoryDetailData.title,
            style = MaterialTheme.typography.titleMedium,
            color = animatedColor.value,
            modifier = Modifier.fillMaxWidth().padding(10.dp)
        )
    }

}
