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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Check
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
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextIndent
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fatih.prayertime.R
import com.fatih.prayertime.data.remote.dto.qurandto.Ayah
import com.fatih.prayertime.data.remote.dto.qurandto.SurahInfo
import com.fatih.prayertime.util.composables.ErrorView
import com.fatih.prayertime.util.composables.LoadingView
import com.fatih.prayertime.util.extensions.toText
import com.fatih.prayertime.util.model.event.QuranDetailScreenEvent
import com.fatih.prayertime.util.model.state.QuranDetailScreenState
import com.fatih.prayertime.util.model.state.QuranSettingsState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun QuranDetailScreen(surahNumber : Int,bottomPadding: Dp,topPadding : Dp, viewModel: QuranDetailScreenViewModel = hiltViewModel()) {
    val quranDetailState by viewModel.quranDetailScreenState.collectAsStateWithLifecycle()
    val quranSettingsState by viewModel.quranSettingsState.collectAsStateWithLifecycle()
    val selectedSurah = remember(quranDetailState) { quranDetailState.selectedSurah }
    val coroutineScope = rememberCoroutineScope()
    var shrinkDelay by remember { mutableLongStateOf(7000L) }
    var showHud by remember { mutableStateOf(true) }
    LaunchedEffect(key1 = showHud, key2 = Unit) {
        while (shrinkDelay > 0) {
            shrinkDelay -= 1000L
            delay(1000L)
        }
        if (showHud) showHud = false
    }
    LaunchedEffect(key1 = surahNumber) {
        viewModel.updateSurahNumber(surahNumber)
    }

    when {
        quranDetailState.isLoading -> {
            LoadingView()
        }
        quranDetailState.isError != null -> {
            println("error")
            ErrorView(quranDetailState.isError?: "Unknown error occurred") {
                coroutineScope.launch(Dispatchers.IO) {
                    viewModel.getSelectedSurah()
                }
            }
        }
        quranDetailState.selectedSurah != null -> {
            Box( modifier = Modifier
                .fillMaxSize()
                .clickable {
                    showHud = !showHud
                    shrinkDelay = 7000L
                }
                .padding(bottom = bottomPadding, top = topPadding)) {

                Spacer(modifier = Modifier.height(16.dp))
                QuranDetailContent(selectedSurah = selectedSurah!!, modifier = Modifier.fillMaxSize(1f),quranDetailState)
                BottomNavigationRow(viewModel,showHud,quranDetailState, quranSettingsState)

            }
            QuranDetailTopBar(selectedSurah = selectedSurah!!, showHud = showHud)
            QuranSettingsBottomSheet(
                state = quranSettingsState,
                onEvent = viewModel::onSettingsEvent
            ) { viewModel.onSettingsEvent(QuranDetailScreenEvent.ToggleSettingsSheet) }
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
fun QuranDetailContent(selectedSurah: SurahInfo, modifier: Modifier = Modifier,state: QuranDetailScreenState) {
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val density = LocalDensity.current
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp

    val centerOffset = with(density) {
        (screenHeight / 4).toPx().toInt()
    }

    LaunchedEffect(state.selectedAyahNumber) {
        if (state.selectedAyahNumber > 0) {
            coroutineScope.launch {
                listState.animateScrollToItem(
                    index = state.selectedAyahNumber- 1,
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
fun AyahCard(ayah: Ayah,state: QuranDetailScreenState) {
    val currentAyah = remember(state.selectedAyahNumber){state.selectedAyahNumber}
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
    viewModel: QuranDetailScreenViewModel,
    showHud: Boolean,
    quranDetailScreenState: QuranDetailScreenState,
    quranSettingsState: QuranSettingsState
) {
    val audioPlayerState = viewModel.audioPlayerState.collectAsStateWithLifecycle()
    val isPlaying = remember(audioPlayerState.value.audioPlaying){audioPlayerState.value.audioPlaying}
    val isLoading = remember(audioPlayerState.value.audioLoading){audioPlayerState.value.audioLoading}
    val currentAyah = remember(quranDetailScreenState.selectedAyahNumber){quranDetailScreenState.selectedAyahNumber}
    val autoHidePlayer = remember(quranSettingsState.autoHidePlayer) { quranSettingsState.autoHidePlayer }
    val selectedSurah = remember(quranDetailScreenState.selectedSurah){quranDetailScreenState.selectedSurah}
    val audioProgress = remember(audioPlayerState.value.currentAudioPosition) {audioPlayerState.value.currentAudioPosition}
    println("audioProgres $audioProgress")
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
                        enabled = currentAyah > 1 && !isLoading
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.KeyboardArrowLeft,
                            contentDescription = "Önceki",
                            tint = if (currentAyah > 1 && !isLoading) 
                                    MaterialTheme.colorScheme.onPrimaryContainer 
                                  else 
                                    MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.5f)
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
                        enabled = currentAyah < (selectedSurah?.ayahs?.size ?: 0) && !isLoading
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.KeyboardArrowRight,
                            contentDescription = "Sonraki",
                            tint = if (currentAyah < (selectedSurah?.ayahs?.size ?: 0) && !isLoading)
                                    MaterialTheme.colorScheme.onPrimaryContainer
                                  else
                                    MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.5f)
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
    state: QuranSettingsState,
    onEvent: (QuranDetailScreenEvent) -> Unit,
    onDismiss: () -> Unit
) {
    var showSpeedDialog by remember { mutableStateOf(false) }
    var showTranslationDialog by remember { mutableStateOf(false) }
    var showReciterDialog by remember { mutableStateOf(false) }
    var showTransliterationDialog by remember { mutableStateOf(false) }

    if (showSpeedDialog) {
        PlaybackSpeedDialog(
            currentSpeed = state.playbackSpeed,
            onSpeedSelected = { speed -> onEvent(QuranDetailScreenEvent.SetPlaybackSpeed(speed)) },
            onDismiss = { showSpeedDialog = false }
        )
    }
    if (state.showCacheInfo) {
        CacheInfoDialog(onEvent)
    }
    // Translation Selection Dialog
    if (showTranslationDialog) {
        SelectionDialog(
            title = "Çeviri Seçin",
            names = state.translationList.map { it.toText() },
            selectedItem = state.selectedTranslation,
            onItemSelected = { selected,index ->
                onEvent(QuranDetailScreenEvent.SetTranslation(selected))
                showTranslationDialog = false
            },
            onDismiss = { showTranslationDialog = false }
        )
    }

    // Reciter Selection Dialog
    if (showReciterDialog) {
        SelectionDialog(
            title = "Okuyucu Seçin",
            names = state.reciterList.map { it.toText() },
            selectedItem = state.selectedReciter,
            onItemSelected = { selected,index ->
                onEvent(QuranDetailScreenEvent.SetReciter(selected,index))
                showReciterDialog = false
            },
            onDismiss = { showReciterDialog = false }
        )
    }

    // Transliteration Selection Dialog
    if (showTransliterationDialog) {
        SelectionDialog(
            title = "Transliterasyon Seçin",
            names = state.transliterationList.map { it.key },
            selectedItem = state.selectedTransliteration,
            onItemSelected = {  selected,index ->
                onEvent(QuranDetailScreenEvent.SetTransliteration(selected))
                showTransliterationDialog = false
            },
            onDismiss = { showTransliterationDialog = false }
        )
    }


    if (state.showSettings) {
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
                SettingsGroup(title = "Dil ve Metin Ayarları") {
                    SettingsRow(
                        title = "Okuyucu",
                        subtitle = state.selectedReciter,
                        onClick = { showReciterDialog = true }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(text = state.selectedReciter)
                            Icon(
                                imageVector = Icons.AutoMirrored.Default.KeyboardArrowRight,
                                contentDescription = null
                            )
                        }
                    }

                    SettingsRow(
                        title = "Çeviri",
                        subtitle = state.selectedTranslation,
                        onClick = { showTranslationDialog = true }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(text = state.selectedTranslation)
                            Icon(
                                imageVector = Icons.AutoMirrored.Default.KeyboardArrowLeft,
                                contentDescription = null
                            )
                        }
                    }

                    SettingsRow(
                        title = "Transliterasyon",
                        subtitle = state.transliterationList[state.selectedTransliteration] ?: "",
                        onClick = { showTransliterationDialog = true }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(text = state.transliterationList[state.selectedTransliteration] ?: "")
                            Icon(
                                imageVector = Icons.AutoMirrored.Default.KeyboardArrowRight,
                                contentDescription = null
                            )
                        }
                    }
                }
                SettingsGroup(title = "Oynatma Ayarları") {
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
                            onCheckedChange = {
                                onEvent(QuranDetailScreenEvent.ToggleCacheInfoDialog)
                            }
                        )
                    }

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
                                imageVector = Icons.AutoMirrored.Default.KeyboardArrowRight,
                                contentDescription = null
                            )
                        }
                    }

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
                                imageVector = Icons.AutoMirrored.Default.KeyboardArrowRight,
                                contentDescription = null
                            )
                        }
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

@Composable
private fun SelectionDialog(
    title: String,
    names: List<String>,
    selectedItem: String,
    onItemSelected: (String, Int) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            LazyColumn {
                itemsIndexed(names) { index,name ->
                    SelectionItem(
                        text = name,
                        isSelected = name == selectedItem,
                        onClick = { onItemSelected(name,index) }
                    )
                }
            }
        },
        confirmButton = { }
    )
}

@Composable
private fun SelectionItem(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        color = if (isSelected)
            MaterialTheme.colorScheme.primaryContainer
        else
            Color.Transparent,
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge,
                color = if (isSelected)
                    MaterialTheme.colorScheme.onPrimaryContainer
                else
                    MaterialTheme.colorScheme.onSurface
            )
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}