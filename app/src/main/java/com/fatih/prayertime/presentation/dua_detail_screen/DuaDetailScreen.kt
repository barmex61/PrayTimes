package com.fatih.prayertime.presentation.dua_detail_screen

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.fatih.prayertime.data.remote.dto.duadto.DuaCategoryDetail
import com.fatih.prayertime.presentation.dua_category_detail_screen.DuaViewModel
import com.fatih.prayertime.util.LoadingView
import com.fatih.prayertime.util.TitleView
import com.fatih.prayertime.util.capitalizeFirstLetter
import kotlin.random.Random

@Composable
fun DuaDetailScreen(bottomPaddingValues : Dp, duaViewModel : DuaViewModel) {

    val duaDetail by duaViewModel.duaDetail.collectAsState()

    if (duaDetail == null){
        LoadingView()
    }else{
        Box(modifier = Modifier.fillMaxSize(1f).padding(bottom = bottomPaddingValues), contentAlignment = Alignment.Center){
            DuaDetailCard(duaDetail!!)
        }
    }
    TitleView("Dua Detail")
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
            .padding(10.dp)
            .graphicsLayer {
                translationX = translation.value.dp.toPx()
                translationY = translation.value.dp.toPx()
            }

    ) {
        Column(modifier = Modifier .verticalScroll(state = scrollState).padding(horizontal = 16.dp, vertical = 16.dp)) {
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
