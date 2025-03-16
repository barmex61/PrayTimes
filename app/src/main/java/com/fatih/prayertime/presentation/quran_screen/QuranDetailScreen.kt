package com.fatih.prayertime.presentation.quran_screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fatih.prayertime.data.remote.dto.qurandto.SurahInfo
import com.fatih.prayertime.util.composables.ErrorView
import com.fatih.prayertime.util.composables.LoadingView
import com.fatih.prayertime.util.extensions.toText
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.delay

@Composable
fun QuranDetailScreen(bottomPadding: Dp, viewModel: QuranViewModel) {
    val state by viewModel.state.collectAsState()
    val selectedSurah = remember(state) { state.selectedSurah }
    var shrinkDelay by remember { mutableLongStateOf(2000L) }
    var showHud by remember { mutableStateOf(true) }
    LaunchedEffect(key1 = showHud, key2 = Unit) {
        while (shrinkDelay > 0) {
            shrinkDelay -= 1000L
            delay(1000L)
        }
        if (showHud) showHud = false
    }
    when {
        state.isLoading -> {
            println("loading")
            LoadingView()
        }
        state.error != null -> {
            ErrorView(state.error ?: "Unknown error occurred") {
                viewModel.getSelectedSurah(state.selectedSurahNumber) {}
            }
        }
        state.selectedSurah != null -> {
            Box(contentAlignment = Alignment.BottomCenter, modifier = Modifier
                .fillMaxSize()
                .padding(bottom = bottomPadding)) {

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable {
                            showHud = true
                            shrinkDelay = 2000L
                        },
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    QuranDetailTopBar(selectedSurah = selectedSurah!!, showHud = showHud)
                    Spacer(modifier = Modifier.height(16.dp))
                    QuranDetailContent(selectedSurah = selectedSurah, modifier = Modifier.weight(1f))

                }
                BottomNavigationRow(showHud)

            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuranDetailTopBar(selectedSurah: SurahInfo, showHud: Boolean) {
    AnimatedVisibility(
        visible = showHud,
        enter = slideInVertically() + fadeIn(),
        exit = slideOutVertically() + fadeOut()
    ) {
        CenterAlignedTopAppBar(
            title = {
                Text(text = selectedSurah.turkishName!!)
            },
            colors = TopAppBarDefaults.largeTopAppBarColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                scrolledContainerColor = MaterialTheme.colorScheme.primaryContainer
            )
        )
    }
}

@Composable
fun QuranDetailContent(selectedSurah: SurahInfo, modifier: Modifier = Modifier) {
    val scrollState = rememberScrollState()
    Card(
        modifier = modifier
            .animateContentSize()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        elevation = CardDefaults.elevatedCardElevation(8.dp)
    ) {
        Box(modifier = Modifier
            .fillMaxSize(1f)
            .verticalScroll(scrollState)
            .padding(16.dp)) {
            selectedSurah.
        }
    }
}

@Composable
fun BottomNavigationRow(showHud: Boolean) {
    AnimatedVisibility(
        visible = showHud,
        enter = slideInVertically(){height -> height} + fadeIn(),
        exit = slideOutVertically(){height -> height} + fadeOut()
    ) {
        Card(
            modifier = Modifier
                .padding(vertical = 28.dp, horizontal = 32.dp)
                .fillMaxWidth()
                .height(90.dp)
                .fillMaxSize(1f)
                .animateContentSize()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
            elevation = CardDefaults.elevatedCardElevation(8.dp)
        ) {
            Row() {
            }
        }

    }

}