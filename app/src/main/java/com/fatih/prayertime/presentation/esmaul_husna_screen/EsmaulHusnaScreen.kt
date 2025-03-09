package com.fatih.prayertime.presentation.esmaul_husna_screen


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
import androidx.hilt.navigation.compose.hiltViewModel
import com.fatih.prayertime.domain.model.EsmaulHusna
import com.fatih.prayertime.util.composables.ErrorView
import com.fatih.prayertime.util.composables.LoadingView
import com.fatih.prayertime.util.composables.TitleView
import com.fatih.prayertime.util.model.state.Status
import kotlin.random.Random

@Composable
fun EsmaulHusnaScreen(bottomPaddingValues: Dp,esmaulHusnaViewModel: EsmaulHusnaViewModel = hiltViewModel()) {
    val esmaulHusnaState by esmaulHusnaViewModel.esmaulHusnaState.collectAsState()
    when(esmaulHusnaState.status){
        Status.ERROR ->{
            ErrorView(esmaulHusnaState.message?:"Error occurred") {
                esmaulHusnaViewModel.loadEsmaulHusna()
            }
        }
        Status.LOADING ->{
            LoadingView()
        }
        Status.SUCCESS->{
            LazyVerticalStaggeredGrid(
                columns = StaggeredGridCells.Fixed(2),
                modifier = Modifier.padding(bottom = bottomPaddingValues)
            ) {
                items(esmaulHusnaState.data!!) { esmaulHusna ->
                    EsmaulHusnaCard(esmaulHusna)
                }
            }
        }
    }

    TitleView("Esmaül Hüsna")
}

@Composable
fun EsmaulHusnaCard(esmaulHusna: EsmaulHusna) {
    val infiniteTransition = rememberInfiniteTransition()
    var expanded by remember { mutableStateOf(false) }
    val translation = infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = if (Random.nextBoolean()) Random.nextFloat() * 4f + 1f else Random.nextFloat() * -4f - 1f,
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
        border = BorderStroke(1.dp,MaterialTheme.colorScheme.primary),
        onClick = {
            expanded = !expanded
        }

    ) {
        Column(
            modifier = Modifier.padding(16.dp).animateContentSize(tween(1000))

        ) {
            Text(
                textAlign = TextAlign.Center,
                text = esmaulHusna.arabicName,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                textAlign = TextAlign.Center,
                text = esmaulHusna.name,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                textAlign = TextAlign.Center,
                text = if(!expanded) esmaulHusna.shortDescription else esmaulHusna.longDescription,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                textAlign = TextAlign.End,
                text = if (!expanded) "Devamını gör..." else "Küçült",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}