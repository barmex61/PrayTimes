package com.fatih.prayertime.presentation.dua_screens.dua_detail_screen

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.fatih.prayertime.R
import com.fatih.prayertime.data.remote.dto.duadto.DuaCategoryDetail
import com.fatih.prayertime.presentation.dua_screens.dua_categories_screen.DuaCategoriesViewModel
import kotlin.random.Random
import com.fatih.prayertime.util.extensions.capitalizeFirstLetter
import com.fatih.prayertime.util.composables.LoadingView
import com.fatih.prayertime.util.composables.TitleView

@Composable
fun DuaDetailScreen(
    modifier: Modifier,
    duaId: Int,
    categoryId: Int,
    viewModel: DuaDetailViewModel = hiltViewModel()
) {
    val duaDetail by viewModel.duaDetail.collectAsState()
    val isFavorite by viewModel.isFavorite.collectAsState()

    LaunchedEffect(duaId) {
        viewModel.updateDuaCategoryId(categoryId)
        viewModel.updateDuaId(duaId)
    }

    if (duaDetail == null) {
        LoadingView()
    } else {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            DuaDetailCard(duaDetail!!)
            AddFavoriteFab(
                isFavorite = isFavorite,
                viewModel = viewModel
            )
        }
    }
    TitleView("Dua Detail")
}

@Composable
fun BoxScope.AddFavoriteFab(isFavorite: Boolean,viewModel: DuaDetailViewModel) {
    val infiniteTransition = rememberInfiniteTransition()

    val fabButtonTransition = infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -200f,
        animationSpec = infiniteRepeatable(tween(6000), repeatMode = RepeatMode.Reverse)
    )
    val fabButtonRotate = animateFloatAsState(
        targetValue = if (isFavorite) 360f else 0f,
    )
    FloatingActionButton(
        onClick = {
            viewModel.toggleFavorite()
        },
        modifier = Modifier
            .padding(16.dp)
            .align(Alignment.BottomEnd)
            .graphicsLayer {
                translationY = fabButtonTransition.value
                rotationX = fabButtonRotate.value
            }
    ) {
        Icon(
            imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
            contentDescription = if (isFavorite) {
                stringResource(id = R.string.remove_from_favorites)
            } else {
                stringResource(id = R.string.add_to_favorites)
            },
            tint = if (isFavorite) Color.Red else Color.Gray
        )
    }
}

@Composable
fun DuaDetailCard(duaDetail: DuaCategoryDetail) {
    val infiniteTransition = rememberInfiniteTransition()
    val scrollState = rememberScrollState()
    val translation = infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = if (Random.nextBoolean()) Random.nextFloat() * 1f + 1f else Random.nextFloat() * -1f - 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000),
            repeatMode = RepeatMode.Reverse
        )
    )
    Card(
        elevation = CardDefaults.cardElevation(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        modifier = Modifier
            .fillMaxWidth(1f)
            .padding(10.dp)
            .graphicsLayer {
                translationX = translation.value.dp.toPx()
                translationY = translation.value.dp.toPx()
            }

    ) {
        Column(modifier = Modifier
            .verticalScroll(state = scrollState)
            .padding(horizontal = 16.dp, vertical = 16.dp)) {
            Text(
                text = duaDetail.arabic,
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = duaDetail.latin.capitalizeFirstLetter(),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(modifier = Modifier.height(22.dp))
            Text(
                text = duaDetail.translation,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(modifier = Modifier.height(22.dp))
            Text(
                text = duaDetail.fawaid,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(modifier = Modifier.height(22.dp))
            Text(
                text = "Source: ${duaDetail.source}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Category: ${duaDetail.categoryName}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}
