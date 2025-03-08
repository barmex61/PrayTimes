package com.fatih.prayertime.presentation.dua_categories_screen

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
import androidx.compose.foundation.lazy.staggeredgrid.itemsIndexed
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.fatih.prayertime.R
import com.fatih.prayertime.data.remote.dto.duadto.DuaCategoryData
import com.fatih.prayertime.util.Constants.colors
import com.fatih.prayertime.util.Constants.duaCategory
import com.fatih.prayertime.util.Constants.screens
import com.fatih.prayertime.util.LoadingView
import com.fatih.prayertime.util.TitleView
import com.fatih.prayertime.util.navigateToScreen

import java.util.Locale
import kotlin.random.Random

@Composable
fun DuaCategoriesScreen(bottomPaddingValues: Dp, navController: NavController) {
    Box(modifier = Modifier.fillMaxSize(1f).padding(bottom = bottomPaddingValues), contentAlignment = Alignment.Center){
        if (duaCategory != null){
            DuaCategoriesGridView(duaCategory!!.data, navController )
        }else{
            LoadingView()
        }

    }
    TitleView("Dua Topics")
}

@Composable
fun DuaCategoriesGridView(duaCategoryDataList: List<DuaCategoryData>, navController: NavController) {
    LazyVerticalStaggeredGrid (
        columns = StaggeredGridCells.Fixed(2),
        verticalItemSpacing = 20.dp,
        horizontalArrangement = Arrangement.spacedBy(20.dp),
        contentPadding = PaddingValues(bottom = 40.dp, top = 40.dp, start = 10.dp, end = 10.dp),
    )  {
        itemsIndexed(duaCategoryDataList) {index, duaCategoryData ->
            DuaCategoryCard(duaCategoryData,index, navController)
        }
    }
}

@Composable
fun DuaCategoryCard(duaCategoryData :DuaCategoryData,index: Int, navController: NavController) {
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
        onClick = {
            val route = screens[4].route.replace("{categoryIndex}","$index")
            navController.navigateToScreen(route)
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