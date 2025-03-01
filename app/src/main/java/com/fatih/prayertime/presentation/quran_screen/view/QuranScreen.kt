package com.fatih.prayertime.presentation.quran_screen.view

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.fatih.prayertime.domain.model.EsmaulHusna
import com.fatih.prayertime.util.Constants.esmaulHusnaList

@Composable
fun QuranScreen(bottomPaddingValues: Dp) {
    var expandedCardId by remember { mutableStateOf<String?>(null) }

    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(2),
        modifier = Modifier.padding(bottom = bottomPaddingValues)
    ) {
        items(esmaulHusnaList) { esmaulHusna ->
            val isExpanded = esmaulHusna.name == expandedCardId
            EsmaulHusnaCard(esmaulHusna, isExpanded) {
                expandedCardId = if (isExpanded) null else esmaulHusna.name
            }
        }
    }
}

@Composable
fun EsmaulHusnaCard(esmaulHusna: EsmaulHusna, isExpanded: Boolean, onCardClick: () -> Unit) {

        Card(
            elevation = CardDefaults.cardElevation(8.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
            modifier = Modifier
                .animateContentSize(tween(1000))
                .padding(bottom = 15.dp, top = 5.dp,start = 10.dp, end = 10.dp),
            border = BorderStroke(1.dp,MaterialTheme.colorScheme.primary),
            onClick = {
                onCardClick()
            }

        ) {
            Column(
                modifier = Modifier.padding(16.dp)
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
                    text = if(!isExpanded) esmaulHusna.shortDescription else esmaulHusna.longDescription,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

}