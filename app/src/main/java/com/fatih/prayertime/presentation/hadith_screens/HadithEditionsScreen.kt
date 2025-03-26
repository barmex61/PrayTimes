package com.fatih.prayertime.presentation.hadith_screens

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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.fatih.prayertime.R
import com.fatih.prayertime.data.remote.dto.hadithdto.Collection
import com.fatih.prayertime.data.remote.dto.hadithdto.Edition
import com.fatih.prayertime.util.composables.ErrorView
import com.fatih.prayertime.util.composables.LoadingView
import com.fatih.prayertime.util.composables.TitleView
import com.fatih.prayertime.util.config.NavigationConfig.screens
import com.fatih.prayertime.util.config.ThemeConfig.colors
import com.fatih.prayertime.util.extensions.navigateToScreen
import com.fatih.prayertime.util.extensions.toList
import com.fatih.prayertime.util.model.state.Resource
import com.fatih.prayertime.util.model.state.Status
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import kotlin.random.Random

@Composable
fun HadithEditionsScreen(modifier: Modifier, navController: NavController, hadithEditionsViewModel: HadithEditionsViewModel = hiltViewModel()) {
    val hadithEdition by hadithEditionsViewModel.hadithEditions.collectAsState(Resource.loading())

    when(hadithEdition.status){
        Status.LOADING -> {
            LoadingView()
        }
        Status.SUCCESS -> {
            LazyVerticalStaggeredGrid(
                columns = StaggeredGridCells.Fixed(2),
                modifier = modifier.fillMaxSize(1f)
            ) {
                val hadithEditionsList = hadithEdition.data!!.toList()
                items(hadithEditionsList) { hadithEdition ->
                    HadithEditionCard(hadithEdition,navController)
                }
            }
        }
        Status.ERROR -> {
            ErrorView(hadithEdition.message?:"Unknown Error"){
                hadithEditionsViewModel.triggerRetry()
            }
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
        painter = rememberAsyncImagePainter(R.drawable.hadith),
        contentDescription = "Hadith",
        modifier = Modifier.size(150.dp),
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
                    val encodedUrl = URLEncoder.encode(collection.linkmin, StandardCharsets.UTF_8.toString())
                    val route = screens[6].route.replace("{collectionPath}", encodedUrl)
                    navController.navigateToScreen(route)
                },
            text = collection.language,
            color = Color.White,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.size(7.dp))
    }
}
