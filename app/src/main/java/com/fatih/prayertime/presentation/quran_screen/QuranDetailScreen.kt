package com.fatih.prayertime.presentation.quran_screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomSheetDefaults

import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextIndent
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fatih.prayertime.R
import com.fatih.prayertime.data.remote.dto.qurandto.Ayah
import com.fatih.prayertime.data.remote.dto.qurandto.SurahInfo
import com.fatih.prayertime.util.composables.ErrorView
import com.fatih.prayertime.util.composables.LoadingView
import com.fatih.prayertime.util.model.event.QuranDetailScreenEvent
import com.fatih.prayertime.util.model.state.QuranScreenState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun QuranDetailScreen(bottomPadding: Dp,topPadding : Dp, viewModel: QuranViewModel) {
    val quranScreenState by viewModel.quranScreenState.collectAsState()
    val selectedSurah = remember(quranScreenState) { quranScreenState.selectedSurah }
    var shrinkDelay by remember { mutableLongStateOf(7000L) }
    var showHud by remember { mutableStateOf(true) }
    println("QuranDetailScreen:")
    LaunchedEffect(key1 = showHud, key2 = Unit) {
        while (shrinkDelay > 0) {
            shrinkDelay -= 1000L
            delay(1000L)
        }
        if (showHud) showHud = false
    }

    when {
        quranScreenState.isLoading -> {
            LoadingView()
        }
        quranScreenState.error != null -> {
            ErrorView(quranScreenState.error ?: "Unknown error occurred") {
                viewModel.getSelectedSurah(quranScreenState.selectedSurahNumber) {}
            }
        }
        quranScreenState.selectedSurah != null -> {
            Box( modifier = Modifier
                .fillMaxSize()
                .clickable {
                    showHud = !showHud
                    shrinkDelay = 7000L
                }
                .padding(bottom = bottomPadding, top = topPadding)) {

                Spacer(modifier = Modifier.height(16.dp))
                QuranDetailContent(selectedSurah = selectedSurah!!, modifier = Modifier.fillMaxSize(1f),quranScreenState)
                BottomNavigationRow(viewModel,showHud,quranScreenState)

            }
            QuranDetailTopBar(selectedSurah = selectedSurah!!, showHud = showHud)
            QuranSettingsBottomSheet(
                state = quranScreenState,
                onEvent = viewModel::onSettingsEvent,
                onDismiss = { viewModel.onSettingsEvent(QuranDetailScreenEvent.ToggleSettingsSheet) }
            )
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
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.85f),
            )
        )
    }
}

@Composable
fun QuranDetailContent(selectedSurah: SurahInfo, modifier: Modifier = Modifier,state: QuranScreenState) {
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val density = LocalDensity.current
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp

    val centerOffset = with(density) {
        (screenHeight / 4).toPx().toInt()
    }

    LaunchedEffect(state.currentAyahNumber) {
        if (state.currentAyahNumber > 0) {
            coroutineScope.launch {
                listState.animateScrollToItem(
                    index = state.currentAyahNumber - 1,
                    scrollOffset = -centerOffset
                )
            }
        }
    }

    LazyColumn(
        state = listState,
        modifier = modifier
            .padding(horizontal = 8.dp)
    ) {
        items(selectedSurah.ayahs!!) { ayah ->
            AyahCard(ayah, state)
        }
    }
}

@Composable
fun AyahCard(ayah: Ayah,state: QuranScreenState) {
    val currentAyah = remember(state.currentAyahNumber){state.currentAyahNumber}
    val ayahNumber = remember{ayah.numberInSurah}
    val ayahColor = animateColorAsState(
        animationSpec = tween(durationMillis = 500),
        targetValue = if (currentAyah == ayahNumber) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onPrimaryContainer
    )
    Box{
        Column(
            Modifier
                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(6.dp))
                .background(
                    color = if (currentAyah == ayahNumber) MaterialTheme.colorScheme.primaryContainer else Color.Transparent,
                )
                .padding(16.dp)

        ) {
            val annotatedString = buildAnnotatedString {
                withStyle(
                    style = ParagraphStyle(
                        textIndent = TextIndent(firstLine = 22.sp)
                    )
                ) {
                    append(ayah.text)
                }
            }
            Text(

                text = annotatedString,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.End,
                style = MaterialTheme.typography.headlineLarge,
                color = ayahColor.value

            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = ayah.textTransliteration ?: "",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Start,
                style = MaterialTheme.typography.bodyMedium,
                color = ayahColor.value
            )
        }
        Box(modifier = Modifier
            .padding(8.dp)
            .size(32.dp), contentAlignment = Alignment.Center){
            Icon(painter = painterResource(R.drawable.ayah), contentDescription = "Ayah",tint= Color.Unspecified)
            Text(text = ayah.numberInSurah.toString(), style = MaterialTheme.typography.bodyMedium,color = Color.Black)
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BoxScope.BottomNavigationRow(
    viewModel: QuranViewModel,
    showHud: Boolean,
    quranDetailScreenState: QuranScreenState,
) {
    val audioPlayerState = viewModel.audioPlayerState.collectAsStateWithLifecycle()
    val isPlaying = remember(audioPlayerState.value.audioPlaying){audioPlayerState.value.audioPlaying}
    val isLoading = remember(audioPlayerState.value.audioLoading){audioPlayerState.value.audioLoading}
    val currentAyah = remember(quranDetailScreenState.currentAyahNumber){quranDetailScreenState.currentAyahNumber}
    val autoHidePlayer = remember(quranDetailScreenState.autoHidePlayer) { quranDetailScreenState.autoHidePlayer }
    val selectedSurah = remember(quranDetailScreenState.selectedSurah){quranDetailScreenState.selectedSurah}
    val audioProgress = remember(audioPlayerState.value.currentAudioPosition) {audioPlayerState.value.currentAudioPosition}

    AnimatedVisibility(
        modifier = Modifier.align(Alignment.BottomCenter),
        visible = if (autoHidePlayer) showHud else true,
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
                        text = selectedSurah?.englishName ?: "",
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
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.KeyboardArrowRight,
                            contentDescription = "Sonraki",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }

                // Sağ taraf - Ayarlar
                IconButton(onClick = {
                    viewModel.onSettingsEvent(QuranDetailScreenEvent.ToggleSettingsSheet)
                }) {
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuranSettingsBottomSheet(
    state: QuranScreenState,
    onEvent: (QuranDetailScreenEvent) -> Unit,
    onDismiss: () -> Unit
) {
    var showSpeedDialog by remember { mutableStateOf(false) }

    if (showSpeedDialog) {
        PlaybackSpeedDialog(
            currentSpeed = state.playbackSpeed,
            onSpeedSelected = { speed -> onEvent(QuranDetailScreenEvent.SetPlaybackSpeed(speed)) },
            onDismiss = { showSpeedDialog = false }
        )
    }
    if (state.showCacheInfoDialog) {
        CacheInfoDialog(onEvent)
    }

    if (state.showSettingsSheet) {
        ModalBottomSheet(
            onDismissRequest = onDismiss,
            dragHandle = { BottomSheetDefaults.DragHandle() },
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                // Playback Settings Group
                SettingsGroup(title = "Oynatma Ayarları") {
                    // Auto-hide player control
                    SettingsRow(
                        title = "Otomatik gizle",
                        subtitle = "Oynatıcı kontrollerini 7 saniye sonra otomatik gizle",
                        onClick = { onEvent(QuranDetailScreenEvent.ToggleAutoHidePlayer) }
                    ) {
                        Checkbox(
                            checked = state.autoHidePlayer,
                            onCheckedChange = { onEvent(QuranDetailScreenEvent.ToggleAutoHidePlayer) }
                        )
                    }

                    SettingsRow(
                        title = "Ses Verilerini Kaydet",
                        subtitle = if (state.shouldCacheAudio)
                            "Çevrimdışı dinleme için ses verileri kaydediliyor"
                        else
                            "Ses verileri kaydedilmiyor",
                        onClick = { onEvent(QuranDetailScreenEvent.ToggleCacheInfoDialog) }
                    ) {
                        Switch(
                            checked = state.shouldCacheAudio,
                            onCheckedChange = { onEvent(QuranDetailScreenEvent.ToggleCacheInfoDialog) }
                        )
                    }

                    // Playback Speed
                    SettingsRow(
                        title = "Oynatma hızı",
                        subtitle = "${state.playbackSpeed}x",
                        onClick = {showSpeedDialog = true }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(text = "${state.playbackSpeed}x")
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowRight,
                                contentDescription = null
                            )
                        }
                    }

                    // Playback Mode
                    SettingsRow(
                        title = "Oynatma modu",
                        subtitle = if (state.playByVerse) "Ayet ayet" else "Sure sure",
                        onClick = { onEvent(QuranDetailScreenEvent.TogglePlaybackMode) }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = if (state.playByVerse) "Ayet ayet" else "Sure sure",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowRight,
                                contentDescription = null
                            )
                        }
                    }
                }

                // Recitation Settings Group
                SettingsGroup(title = "Okuyucu Ayarları") {
                    // Reciter Selection
                    SettingsRow(
                        title = "Okuyucu",
                        subtitle = state.selectedReciter,
                        onClick = { /* Show reciter selection */ }
                    ) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowRight,
                            contentDescription = null
                        )
                    }
                }

                // Text Settings Group
                SettingsGroup(title = "Metin Ayarları") {
                    // Translation
                    SettingsRow(
                        title = "Çeviri",
                        subtitle = state.selectedTranslation,
                        onClick = { /* Show translation selection */ }
                    ) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowRight,
                            contentDescription = null
                        )
                    }

                    // Transliteration
                    SettingsRow(
                        title = "Latin harfleri",
                        subtitle = state.selectedTransliteration,
                        onClick = { /* Show transliteration selection */ }
                    ) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowRight,
                            contentDescription = null
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CacheInfoDialog(onEvent: (QuranDetailScreenEvent) -> Unit) {
    AlertDialog(
        onDismissRequest = { onEvent(QuranDetailScreenEvent.ToggleCacheInfoDialog) },
        title = {
            Text("Ses Verilerini Kaydetme")
        },
        text = {
            Column {
                Text(
                    "Ses verilerini cihazınıza kaydettiğinizde şu avantajlara sahip olursunuz:",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                BulletPoint("Çevrimdışı dinleme imkanı")
                BulletPoint("Daha hızlı yükleme süreleri")
                BulletPoint("Daha az internet kullanımı")

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    "Dezavantajları:",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                BulletPoint("Cihazınızda depolama alanı kullanır (Sure başına yaklaşık 1-2 MB)")
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onEvent(QuranDetailScreenEvent.SetShouldCacheAudio(true))
                    onEvent(QuranDetailScreenEvent.ToggleCacheInfoDialog)
                }
            ) {
                Text("Kaydet")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onEvent(QuranDetailScreenEvent.SetShouldCacheAudio(false))
                    onEvent(QuranDetailScreenEvent.ToggleCacheInfoDialog)
                }
            ) {
                Text("Kaydetme")
            }
        }
    )
}


@Composable
private fun BulletPoint(text: String) {
    Row(
        modifier = Modifier.padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text("•")
        Text(text)
    }
}

@Composable
private fun SettingsGroup(
    title: String,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
        content()
        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
private fun SettingsRow(
    title: String,
    subtitle: String? = null,
    onClick: () -> Unit,
    trailing: @Composable () -> Unit
) {
    Surface(
        onClick = onClick,
        color = Color.Transparent
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge
                )
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            trailing()
        }
    }
}

@Composable
fun PlaybackSpeedDialog(
    currentSpeed: Float,
    onSpeedSelected: (Float) -> Unit,
    onDismiss: () -> Unit
) {
    val speeds = listOf(0.25f, 0.5f, 0.75f, 1f, 1.25f, 1.5f, 1.75f, 2f)

    AlertDialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
        modifier = Modifier
            .fillMaxWidth(0.8f)
            .clip(RoundedCornerShape(16.dp)),
        containerColor = MaterialTheme.colorScheme.secondaryContainer,
        title = {
            Text(
                text = "Oynatma hızı",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        },
        text = {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(speeds) { speed ->
                    Surface(
                        onClick = {
                            onSpeedSelected(speed)
                            onDismiss()
                        },
                        color = if (speed == currentSpeed)
                            MaterialTheme.colorScheme.primaryContainer
                        else
                            Color.Transparent,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${speed}x",
                                style = MaterialTheme.typography.bodyLarge,
                                color = if (speed == currentSpeed)
                                    MaterialTheme.colorScheme.onPrimaryContainer
                                else
                                    MaterialTheme.colorScheme.onSurface
                            )
                            if (speed == currentSpeed) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = { }
    )
}