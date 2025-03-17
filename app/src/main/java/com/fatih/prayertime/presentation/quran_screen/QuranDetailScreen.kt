package com.fatih.prayertime.presentation.quran_screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings

import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.fatih.prayertime.R
import com.fatih.prayertime.data.remote.dto.qurandto.Ayah
import com.fatih.prayertime.data.remote.dto.qurandto.SurahInfo
import com.fatih.prayertime.util.composables.ErrorView
import com.fatih.prayertime.util.composables.LoadingView
import com.fatih.prayertime.util.model.state.QuranScreenState
import kotlinx.coroutines.delay

@Composable
fun QuranDetailScreen(bottomPadding: Dp,topPadding : Dp, viewModel: QuranViewModel) {
    val state by viewModel.state.collectAsState()
    val selectedSurah = remember(state) { state.selectedSurah }
    var shrinkDelay by remember { mutableLongStateOf(5000L) }
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
            Box( modifier = Modifier.fillMaxSize()
                .clickable {
                    showHud = true
                    shrinkDelay = 5000L
                }.padding(bottom = bottomPadding, top = topPadding)) {

                QuranDetailTopBar(modifier = Modifier.align(Alignment.TopCenter),selectedSurah = selectedSurah!!, showHud = showHud)
                Spacer(modifier = Modifier.height(16.dp))
                QuranDetailContent(selectedSurah = selectedSurah, modifier = Modifier.fillMaxSize(1f),viewModel)
                BottomNavigationRow(viewModel,showHud, state)

            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuranDetailTopBar(modifier: Modifier,selectedSurah: SurahInfo, showHud: Boolean) {
    AnimatedVisibility(
        visible = showHud,
        enter = slideInVertically() + fadeIn(),
        exit = slideOutVertically() + fadeOut()
    ) {
        CenterAlignedTopAppBar(
            modifier = modifier,
            title = {
                Text(text = selectedSurah.turkishName!!)
            },
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 1f),

            )
        )
    }
}

@Composable
fun QuranDetailContent(selectedSurah: SurahInfo, modifier: Modifier = Modifier,viewModel: QuranViewModel) {
    LazyColumn(
        modifier = modifier
            .padding(horizontal = 8.dp)) {
        items(selectedSurah.ayahs!!) { ayah->
            AyahCard(ayah)
        }
    }

}

@Composable
fun AyahCard(ayah: Ayah) {
    Column(
        Modifier
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(6.dp))
            .padding(16.dp)
    ) {
        Text(
            text = ayah.text,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.End,
            style = MaterialTheme.typography.headlineLarge
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = ayah.textTransliteration ?: "",
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Start,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSecondaryContainer
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BoxScope.BottomNavigationRow(
    viewModel: QuranViewModel,
    showHud: Boolean,
    state: QuranScreenState
) {
    val isPlaying = state.isAudioPlaying
    val isLoading = state.isAudioLoading
    val currentAyah = state.currentAyahNumber
    val selectedSurah = state.selectedSurah
    val audioProgress = state.currentAudioPosition

    AnimatedVisibility(
        modifier = Modifier.align(Alignment.BottomCenter),
        visible = true,
        enter = slideInVertically(){height -> height} + fadeIn(),
        exit = slideOutVertically(){height -> height} + fadeOut()
    ) {
        Column (modifier = Modifier.background(MaterialTheme.colorScheme.primaryContainer)){
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(72.dp)
                    .padding(horizontal = 16.dp)
                ,
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Sol taraf - Sure bilgisi
                Column {
                    Text(
                        text = selectedSurah?.name ?: "",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = "Ayet: $currentAyah",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                    )

                }

                // Orta - Kontrol butonları
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Önceki
                    IconButton(
                        onClick = { viewModel.updateCurrentAyahNumber(-1) },
                        enabled = selectedSurah != null && currentAyah > 0
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.KeyboardArrowLeft,
                            contentDescription = "Önceki",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }

                    // Oynat/Duraklat
                    IconButton(
                        onClick = {
                            if (isPlaying) {
                                viewModel.pauseAudio()
                            } else {
                                viewModel.resumeAudio()
                            }
                        },
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary)
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                color = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(24.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                imageVector = if (isPlaying) ImageVector.vectorResource(R.drawable.baseline_pause_24) else ImageVector.vectorResource(R.drawable.baseline_play_arrow_24),
                                contentDescription = if (isPlaying) "Duraklat" else "Oynat",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }

                    // Sonraki
                    IconButton(
                        onClick = { viewModel.updateCurrentAyahNumber(1) },
                        enabled = selectedSurah != null && currentAyah > 0 && currentAyah < selectedSurah.ayahs!!.size
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.KeyboardArrowRight,
                            contentDescription = "Sonraki",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }

                // Sağ taraf - Ayarlar
                IconButton(onClick = { }) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Ayarlar",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
            Slider(
                modifier = Modifier.padding(horizontal = 16.dp),
                value = audioProgress,
                onValueChange = { viewModel.seekTo(it) },
            )
        }

    }

}