package com.fatih.prayertime.presentation.hadith_screens

import androidx.compose.animation.animateColor
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.fatih.prayertime.data.remote.dto.hadithdto.HadithSectionInfo
import com.fatih.prayertime.domain.model.HadithSectionData
import com.fatih.prayertime.util.config.NavigationConfig.screens
import com.fatih.prayertime.util.extensions.navigateToScreen
import kotlin.random.Random
import kotlin.reflect.full.memberProperties
import com.fatih.prayertime.util.model.state.Status
import com.fatih.prayertime.util.composables.ErrorView
import com.fatih.prayertime.util.composables.LoadingView
import com.fatih.prayertime.util.composables.TitleView
import com.fatih.prayertime.util.config.ThemeConfig.colors
import com.fatih.prayertime.util.utils.HadithUtils.anyToInt
import com.fatih.prayertime.util.utils.HadithUtils.getPropertyName

@Composable
fun HadithCollectionScreen(modifier: Modifier, collectionPath: String, hadithViewModel: HadithViewModel, navController: NavController) {
    val hadithSectionCardDataList by hadithViewModel.hadithSectionCardDataList.collectAsState()
    LaunchedEffect(Unit) {
        hadithViewModel.updateHadithCollectionPath(collectionPath)
    }
    when(hadithSectionCardDataList.status) {
        Status.SUCCESS -> {
            HadithCollectionGridView(hadithSectionCardDataList.data!!,hadithViewModel, navController ,modifier)
        }
        Status.LOADING ->{
            LoadingView()
        }
        Status.ERROR ->{
            ErrorView(message = hadithSectionCardDataList.message.toString()){
                hadithViewModel.triggerHadithRetry()
            }
        }
    }


    TitleView("Hadith Topics")
}

@Composable
fun HadithCollectionGridView(hadithSectionDataList: List<HadithSectionData>, hadithCollectionViewModel: HadithViewModel, navController: NavController, modifier: Modifier) {

    LazyVerticalStaggeredGrid(
        modifier = modifier,
        columns = StaggeredGridCells.Fixed(2),
    ) {
        itemsIndexed(hadithSectionDataList) { index, hadithCollectionCardData ->
            HadithCollectionCard(index,hadithCollectionCardData, hadithCollectionViewModel, navController  )
        }
    }
}


@Composable
fun HadithCollectionCard(index:Int, hadithSectionData: HadithSectionData, hadithCollectionViewModel: HadithViewModel, navController: NavController) {
    val infiniteTransition = rememberInfiniteTransition()
    val randomColor = remember { colors.random() }
    val targetColor = remember { colors.filter { it != randomColor }.random() }
    val translation = infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = if (Random.nextBoolean()) Random.nextFloat() * 4f + 1f else Random.nextFloat() * -4f - 1f,
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

    Card(
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        modifier = Modifier
            .padding(bottom = 15.dp, top = 5.dp, start = 10.dp, end = 10.dp)
            .graphicsLayer {
                translationX = translation.value.dp.toPx()
                translationY = translation.value.dp.toPx()
                rotationZ = translation.value / 2f
            },
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
        onClick = {
            hadithCollectionViewModel.updateSelectedHadithSection(hadithSectionData,index)
            val subRoute = screens[5].route.replace("{collectionPath}","")
            val route = subRoute.replace("{hadithSectionIndex}","")
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
                text = hadithSectionData.section?:"",
                style = MaterialTheme.typography.titleMedium,
                color = animatedColor.value,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            HadithSectionInfo::class.memberProperties.filterIndexed { index, _ ->
                index >= 2
            }.forEach { property ->
                if (hadithSectionData.details != null){
                    val value = property.get(hadithSectionData.details)
                    Text(
                        text = "${property.getPropertyName()}: ${value.anyToInt()?:value}",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodySmall,
                        color = animatedColor.value,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

            }

        }
    }
}