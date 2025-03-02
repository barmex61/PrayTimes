package com.fatih.prayertime.presentation.hadith_screen.view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColor
import androidx.compose.animation.animateColorAsState
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
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.lazy.staggeredgrid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.fatih.prayertime.R
import com.fatih.prayertime.data.remote.dto.hadithdto.Edition
import com.fatih.prayertime.data.remote.dto.hadithdto.HadithEdition
import com.fatih.prayertime.domain.model.EsmaulHusna
import com.fatih.prayertime.presentation.hadith_screen.viewmodel.HadithScreenViewModel
import com.fatih.prayertime.util.ErrorView
import com.fatih.prayertime.util.LoadingView
import com.fatih.prayertime.util.Status
import com.fatih.prayertime.util.toList
import kotlin.random.Random

@Composable
fun HadithScreen(bottomPaddingValues: Dp) {
    val textColors = listOf(
        Color(0xFFAAFFAA), // Green tint
        Color(0xFFFFAAAA), // Red tint
        Color(0xFFAAAAFF), // Blue tint
        Color(0xFFFFFFAA), // Yellow tint
        Color(0xFFAAFFFF), // Cyan tint
        Color(0xFFFFAAFF), // Magenta tint
        Color(0xFFFD615F), // Gray tint
        Color(0xFFB27933), // Brown tint
        Color(0xFF00897B), // Teal tint
        Color(0xFF68B469)  // Olive tint
    )
    val hadithScreenViewModel = hiltViewModel<HadithScreenViewModel>()
    var isVisible by remember { mutableStateOf(false) }
    val hadithEdition by hadithScreenViewModel.hadithEditions.collectAsState()
    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(tween(1500)) + slideInVertically(tween(1000)),
        exit = fadeOut(tween(1500)) + slideOutVertically(tween(1000))
    ){
        when(hadithEdition.status){
            Status.LOADING -> {
                LoadingView()
            }
            Status.SUCCESS -> {
                LazyVerticalStaggeredGrid(
                    columns = StaggeredGridCells.Fixed(2),
                    modifier = Modifier.padding(bottom = bottomPaddingValues)
                ) {
                    val hadithEditionsList = hadithEdition.data!!.toList()
                    items(hadithEditionsList) { hadithEdition ->
                        HadithEditionCard(hadithEdition,textColors)
                    }
                }
            }
            Status.ERROR -> {
                ErrorView(hadithEdition.message?:"Unknown Error")
            }
        }

    }
    LaunchedEffect(Unit) {
        isVisible = true
    }

}

private fun Modifier.blendMode(
    blendMode: BlendMode
) : Modifier {
    return this.drawWithCache {
        val graphicsLayer = obtainGraphicsLayer()
        graphicsLayer.apply {
            record {
                drawContent()
            }
            this.blendMode = blendMode
        }
        onDrawWithContent {
            drawLayer(graphicsLayer)
        }
    }
}

@Composable
fun HadithEditionCard(hadithEdition: Edition,colors : List<Color>) {
    val infiniteTransition = rememberInfiniteTransition()
    var isExpanded by remember { mutableStateOf(false) }
    val randomColor = colors.random()
    val translation = infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = if (Random.nextBoolean()) Random.nextFloat() * 4f + 1f else Random.nextFloat() * -4f - 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000),
            repeatMode = RepeatMode.Reverse
        )
    )
    val animatedColor = infiniteTransition.animateColor(
        initialValue = randomColor ,
        targetValue = colors.filter { it != randomColor }.random(),
        animationSpec = infiniteRepeatable(
            animation = tween(3000),
            repeatMode = RepeatMode.Reverse
        )
    )

    Card(
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        modifier = Modifier
            .padding(bottom = 15.dp, top = 5.dp,start = 10.dp, end = 10.dp)
            .graphicsLayer {
                translationX = translation.value.dp.toPx()
                translationY = translation.value.dp.toPx()
                rotationZ = translation.value / 2f
            },
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
        onClick = {
            isExpanded = !isExpanded
        }

    ) {
        Column(
            modifier = Modifier.padding(16.dp).animateContentSize(tween(1000)),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(R.drawable.hadith),
                contentDescription = "Hadith",
                modifier = Modifier.fillMaxWidth(),
                colorFilter = ColorFilter.lighting(
                    multiply = animatedColor.value,
                    add = Color.Black
                )
            )
            Row (
                verticalAlignment = Alignment.CenterVertically
            ){
                Text(
                    textAlign = TextAlign.Center,
                    text = "Name : ",
                    style = MaterialTheme.typography.bodySmall,
                    color = animatedColor.value,
                )
                Text(
                    modifier = Modifier.fillMaxWidth(1f),
                    textAlign = TextAlign.Center,
                    text = hadithEdition.name,
                    style = MaterialTheme.typography.bodyMedium,
                    color = animatedColor.value,
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically

            ) {
                Text(
                    textAlign = TextAlign.Center,
                    text = "Book : ",
                    style = MaterialTheme.typography.bodySmall,
                    color = animatedColor.value
                )
                Text(
                    modifier = Modifier.fillMaxWidth(1f),
                    textAlign = TextAlign.Center,
                    text = hadithEdition.collection.first().book.replaceFirstChar { oldChar -> oldChar.uppercaseChar() },
                    style = MaterialTheme.typography.bodyLarge,
                    color = animatedColor.value,
                )
            }
            Spacer(modifier = Modifier.height(15.dp))
            if (isExpanded) {
                hadithEdition.collection.forEach { collection ->

                    Text(
                        modifier = Modifier.fillMaxWidth(0.7f).background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    randomColor,
                                    animatedColor.value
                                )
                            ),
                            shape = RoundedCornerShape(10.dp)
                        ).blendMode(BlendMode.Difference),
                        text = collection.language,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                    Spacer(
                        modifier = Modifier.size(7.dp)
                    )
                }

            }
        }
    }
}