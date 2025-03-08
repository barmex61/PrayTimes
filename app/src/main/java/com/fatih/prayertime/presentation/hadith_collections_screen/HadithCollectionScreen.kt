package com.fatih.prayertime.presentation.hadith_collections_screen

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.fatih.prayertime.data.remote.dto.hadithdto.HadithSectionInfo
import com.fatih.prayertime.domain.model.HadithSectionCardData
import com.fatih.prayertime.util.Constants.colors
import com.fatih.prayertime.util.Constants.screens
import com.fatih.prayertime.util.ErrorView
import com.fatih.prayertime.util.LoadingView
import com.fatih.prayertime.util.Status
import com.fatih.prayertime.util.TitleView
import com.fatih.prayertime.util.anyToInt
import com.fatih.prayertime.util.getPropertyName
import com.fatih.prayertime.util.navigateToScreen
import kotlin.random.Random
import kotlin.reflect.full.memberProperties

@Composable
fun HadithCollectionScreen(bottomPaddingValues: Dp,collectionPath : String, hadithCollectionViewModel : HadithCollectionViewModel, navController: NavController) {
    val hadithSectionCardDataList by hadithCollectionViewModel.hadithSectionCardDataList.collectAsState()
    LaunchedEffect(Unit) {
        hadithCollectionViewModel.updateHadithCollectionPath(collectionPath)
    }
    when(hadithSectionCardDataList.status) {
        Status.SUCCESS -> {
            println("success")
            HadithCollectionGridView(hadithSectionCardDataList.data!!,hadithCollectionViewModel, navController ,bottomPaddingValues)
        }
        Status.LOADING ->{
            LoadingView()
        }
        Status.ERROR ->{
            ErrorView(message = hadithSectionCardDataList.message.toString()){
                hadithCollectionViewModel.getHadithCollection()
            }
        }
    }


    TitleView("Hadith Topics")
}

@Composable
fun HadithCollectionGridView(hadithSectionCardDataList: List<HadithSectionCardData>, hadithCollectionViewModel: HadithCollectionViewModel, navController: NavController,bottomPaddingValues: Dp) {

    LazyVerticalStaggeredGrid(
        modifier = Modifier.padding(bottom =bottomPaddingValues ),
        columns = StaggeredGridCells.Fixed(2),
    ) {
        items(hadithSectionCardDataList) { hadithCollectionCardData ->
            HadithCollectionCard(hadithCollectionCardData, hadithCollectionViewModel, navController  )
        }
    }
}


@Composable
fun HadithCollectionCard(hadithSectionCardData: HadithSectionCardData, hadithCollectionViewModel: HadithCollectionViewModel, navController: NavController) {
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
            hadithCollectionViewModel.updateSelectedHadithSection(hadithSectionCardData)
            navController.navigateToScreen(screens[5].route)
        }
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .animateContentSize(tween(1000))

        ) {
            Text(
                textAlign = TextAlign.Center,
                text = hadithSectionCardData.section?:"",
                style = MaterialTheme.typography.titleMedium,
                color = animatedColor.value,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            HadithSectionInfo::class.memberProperties.filterIndexed { index, _ ->
                index >= 2
            }.forEach { property ->
                if (hadithSectionCardData.details != null){
                    val value = property.get(hadithSectionCardData.details)
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