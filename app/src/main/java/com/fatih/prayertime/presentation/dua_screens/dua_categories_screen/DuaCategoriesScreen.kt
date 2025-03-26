package com.fatih.prayertime.presentation.dua_screens.dua_categories_screen

import androidx.compose.animation.animateColor
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.fatih.prayertime.R
import com.fatih.prayertime.data.remote.dto.duadto.DuaCategoryData
import com.fatih.prayertime.util.composables.ErrorView
import com.fatih.prayertime.util.config.NavigationConfig.screens
import com.fatih.prayertime.util.config.ThemeConfig.colors
import com.fatih.prayertime.util.extensions.navigateToScreen
import com.fatih.prayertime.util.composables.LoadingView
import com.fatih.prayertime.util.composables.TitleView
import com.fatih.prayertime.util.model.state.Status
import java.util.Locale
import kotlin.random.Random

@Composable
fun DuaCategoriesScreen(
    modifier: Modifier,
    navController: NavController,
    duaCategoriesViewModel: DuaCategoriesViewModel = hiltViewModel()
) {
    val duaState by duaCategoriesViewModel.duaState
    if (duaState == null){
        LoadingView()
    }else{
        Box(modifier = modifier.fillMaxSize(1f), contentAlignment = Alignment.Center){
            if (duaState != null){
                DuaCategoriesGridView(duaState!!.data){ categoryId ->
                    val route = screens[4].route.replace("{categoryId}","$categoryId")
                    navController.navigateToScreen(route)
                }
            }else{
                LoadingView()
            }

        }
    }


    TitleView("Dua Topics")
}

@Composable
fun DuaCategoriesGridView(duaCategoryDataList: List<DuaCategoryData>, onCategoryClick : (Int) -> Unit) {
    LazyVerticalStaggeredGrid (
        columns = StaggeredGridCells.Fixed(2),
        verticalItemSpacing = 20.dp,
        horizontalArrangement = Arrangement.spacedBy(20.dp),
        contentPadding = PaddingValues(bottom = 40.dp, top = 40.dp, start = 10.dp, end = 10.dp),
    )  {
        items(duaCategoryDataList) {duaCategoryData ->
            DuaCategoryCard(duaCategoryData, onCategoryClick = onCategoryClick)
        }
    }
}

@Composable
fun DuaCategoryCard(duaCategoryData :DuaCategoryData, onCategoryClick : (Int) -> Unit ) {
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
            .graphicsLayer {
                scaleY = scale.value
                translationX = translation.value.dp.toPx()
                translationY = translation.value.dp.toPx()
                rotationZ = translation.value / 2f
            }

            ,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
        onClick ={
            println("onclick")
            onCategoryClick(duaCategoryData.id)
        }
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .animateContentSize(tween(1000))

        ) {
            Text(
                textAlign = TextAlign.Center,
                text = if (Locale.getDefault().language == "tr") duaCategoryData.nameTr else duaCategoryData.name,
                style = MaterialTheme.typography.titleMedium,
                color = animatedColor.value,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                textAlign = TextAlign.Center,
                text = stringResource(R.string.number_of_prayers)+" :  ${duaCategoryData.total}",
                style = MaterialTheme.typography.titleSmall,
                color = animatedColor.value,
                modifier = Modifier.fillMaxWidth()
            )

        }
    }
}