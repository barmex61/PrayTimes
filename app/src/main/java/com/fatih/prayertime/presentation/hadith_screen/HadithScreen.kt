package com.fatih.prayertime.presentation.hadith_screen

import androidx.compose.animation.animateColor
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.fatih.prayertime.R
import com.fatih.prayertime.data.remote.dto.hadithdto.Collection
import com.fatih.prayertime.data.remote.dto.hadithdto.Edition
import com.fatih.prayertime.util.Constants.colors
import com.fatih.prayertime.util.Constants.screens
import com.fatih.prayertime.util.ErrorView
import com.fatih.prayertime.util.LoadingView
import com.fatih.prayertime.util.Status
import com.fatih.prayertime.util.TitleView
import com.fatih.prayertime.util.navigateToScreen
import com.fatih.prayertime.util.toList
import kotlin.random.Random

@Composable
fun HadithScreen(bottomPaddingValues: Dp,navController: NavController,hadithScreenViewModel : HadithScreenViewModel = hiltViewModel()) {
    val hadithEdition by hadithScreenViewModel.hadithEditions.collectAsState()

    when(hadithEdition.status){
        Status.LOADING -> {
            LoadingView()
        }
        Status.SUCCESS -> {
            println("hadith screen")
            LazyVerticalStaggeredGrid(
                columns = StaggeredGridCells.Fixed(2),
                modifier = Modifier.padding(bottom = bottomPaddingValues)
            ) {
                val hadithEditionsList = hadithEdition.data!!.toList()
                items(hadithEditionsList) { hadithEdition ->
                    HadithEditionCard(hadithEdition,navController)
                }
            }
        }
        Status.ERROR -> {
            ErrorView(hadithEdition.message?:"Unknown Error")
        }
    }
    TitleView("Hadith Books")
}


@Composable
fun HadithEditionCard(hadithEdition: Edition,navController: NavController) {
    val infiniteTransition = rememberInfiniteTransition()
    var isExpanded by remember { mutableStateOf(false) }
    val randomColor = remember { colors.random() }
    val targetColor = remember { colors.filter { it != randomColor }.random() }


    val translation = infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = remember { if (Random.nextBoolean()) Random.nextFloat() * 4f + 1f else Random.nextFloat() * -4f - 1f },
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
            .padding(10.dp)
            .graphicsLayer {
                translationX = translation.value.dp.toPx()
                translationY = translation.value.dp.toPx()
                rotationZ = translation.value / 2f
            },
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
        onClick = { isExpanded = !isExpanded }
    ) {
        CardContent(
            hadithEdition = hadithEdition,
            animatedColor = animatedColor.value,
            randomColor = randomColor,
            isExpanded = isExpanded,
            navController = navController
        )
    }
}

@Composable
fun CardContent(
    hadithEdition: Edition,
    animatedColor: Color,
    randomColor: Color,
    isExpanded: Boolean,
    navController: NavController
) {
    Column(
        modifier = Modifier.padding(16.dp).animateContentSize(tween(1000)),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HadithImage(animatedColor)
        HadithInfoRow("Name: ", hadithEdition.name, animatedColor)
        Spacer(modifier = Modifier.height(8.dp))
        HadithInfoRow(
            "Book: ",
            hadithEdition.collection.first().book.replaceFirstChar { it.uppercaseChar() },
            animatedColor
        )
        Spacer(modifier = Modifier.height(15.dp))
        if (isExpanded) {
            LanguageList(hadithEdition.collection, randomColor, animatedColor, navController)
        }
    }
}

@Composable
fun HadithImage(animatedColor: Color) {
    Image(
        painter = painterResource(R.drawable.hadith),
        contentDescription = "Hadith",
        modifier = Modifier.fillMaxWidth(),
        colorFilter = ColorFilter.lighting(
            multiply = animatedColor,
            add = Color.Black
        )
    )
}

@Composable
fun HadithInfoRow(label: String, value: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            textAlign = TextAlign.Center,
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = color
        )
        Text(
            modifier = Modifier.fillMaxWidth(1f),
            textAlign = TextAlign.Center,
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = color
        )
    }
}

@Composable
fun LanguageList(collections: List<Collection>, randomColor: Color, animatedColor: Color,navController: NavController) {
    collections.forEach { collection ->
        Text(
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(randomColor, animatedColor)
                    ),
                    shape = RoundedCornerShape(10.dp)
                )
                .clickable {
                    navController.navigateToScreen(screens[6],collection.linkmin)
                },
            text = collection.language,
            color = Color.White,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.size(7.dp))
    }
}
