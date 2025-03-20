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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
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
import androidx.compose.ui.text.font.FontWeight
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
import com.fatih.prayertime.domain.model.JuzInfo
import com.fatih.prayertime.util.composables.ErrorView
import com.fatih.prayertime.util.composables.LoadingView
import com.fatih.prayertime.util.composables.QuranPlayerBottomBar
import com.fatih.prayertime.util.composables.TitleView
import com.fatih.prayertime.util.extensions.toText
import com.fatih.prayertime.util.model.event.QuranJuzDetailScreenEvent
import com.fatih.prayertime.util.model.state.QuranJuzDetailScreenState
import com.fatih.prayertime.util.model.state.QuranSettingsState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun QuranJuzDetailScreen(
    juzNumber: Int,
    bottomPadding: Dp,
    topPadding: Dp, 
    viewModel: QuranJuzDetailScreenViewModel = hiltViewModel()
) {
    val quranJuzDetailState by viewModel.quranJuzDetailScreenState.collectAsStateWithLifecycle()
    val quranSettingsState by viewModel.quranSettingsState.collectAsStateWithLifecycle()
    val selectedJuz = remember(quranJuzDetailState) { quranJuzDetailState.selectedJuz }
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

    LaunchedEffect(key1 = juzNumber) {
        viewModel.updateJuzNumber(juzNumber)
    }

    when {
        quranJuzDetailState.isLoading -> {
            LoadingView()
        }
        quranJuzDetailState.isError != null -> {
            ErrorView(quranJuzDetailState.isError ?: "Bilinmeyen bir hata oluştu") {
                coroutineScope.launch(Dispatchers.IO) {
                    viewModel.getSelectedJuz()
                }
            }
        }
        quranJuzDetailState.selectedJuz != null -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable {
                        showHud = !showHud
                        shrinkDelay = 7000L
                    }
                    .padding(bottom = bottomPadding, top = topPadding)
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                QuranJuzDetailContent(
                    state = quranJuzDetailState,
                    onSurahClick = { surahNumber -> 
                        viewModel.onSettingsEvent(QuranJuzDetailScreenEvent.SelectSurah(surahNumber))
                    }
                )
                
                QuranPlayerBottomBar(
                    showHud = showHud,
                    isPlaying = false,
                    currentTime = 0L,
                    totalDuration = 0L,
                    audioProgress = 0f,
                    onPlayPauseClick = {

                    },
                    onPreviousClick = {
                        viewModel.updateCurrentAyahNumber(-1)
                    },
                    onNextClick = {
                        viewModel.updateCurrentAyahNumber(1)
                    },
                    onSettingsClick = {
                        viewModel.onSettingsEvent(QuranJuzDetailScreenEvent.ToggleSettingsSheet)
                    },
                    onSeek = { position ->
                        viewModel.seekTo(position)
                    }
                )
            }

            QuranJuzDetailTopBar(selectedJuz = selectedJuz!!, showHud = showHud)
            
            // Ayarlar için bottom sheet (quran settings)
            QuranSettingsBottomSheet(
                state = quranSettingsState,
                onEvent = viewModel::onSettingsEvent
            ) { viewModel.onSettingsEvent(QuranJuzDetailScreenEvent.ToggleSettingsSheet) }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuranJuzDetailTopBar(selectedJuz: JuzInfo, showHud: Boolean) {
    AnimatedVisibility(
        visible = showHud,
        enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut()
    ) {
        CenterAlignedTopAppBar(
            title = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Cüz ${selectedJuz.juzNumber}",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = selectedJuz.juzName,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            },
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
        )
    }
}

@Composable
fun QuranJuzDetailContent(
    state: QuranJuzDetailScreenState,
    onSurahClick: (Int) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Cüz başlığı
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Cüz ${state.selectedJuz?.juzNumber}",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = state.selectedJuz?.juzName ?: "",
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = "Ayet Aralığı: ${state.selectedJuz?.verseRange}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Bu cüzde yer alan sureler listesi
        Text(
            text = "Bu Cüzdeki Sureler",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(horizontal = 16.dp),
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(state.surahsInJuz) { surah ->
                SurahInJuzCard(
                    surah = surah,
                    isSelected = state.selectedSurah?.number == surah.number,
                    onClick = { onSurahClick(surah.number) }
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Seçili surenin ayetleri
        if (state.selectedSurah != null) {
            state.selectedSurah.ayahs?.let { ayahs ->
                Text(
                    text = "${state.selectedSurah.name} Suresi",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(horizontal = 16.dp),
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    itemsIndexed(ayahs) { index, ayah ->
                        AyahCard(
                            ayah = ayah,
                            isSelected = index + 1 == state.selectedAyahNumber
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SurahInJuzCard(
    surah: SurahInfo,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
        animationSpec = tween(300), 
        label = ""
    )
    
    val textColor by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
        animationSpec = tween(300), 
        label = ""
    )
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${surah.number}. ${surah.name}",
                style = MaterialTheme.typography.bodyLarge,
                color = textColor
            )
            
            Text(
                text = "${surah.numberOfAyahs} Ayet",
                style = MaterialTheme.typography.bodySmall,
                color = textColor
            )
        }
    }
}

@Composable
fun AyahCard(
    ayah: Ayah,
    isSelected: Boolean
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
        animationSpec = tween(300), 
        label = ""
    )
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = buildAnnotatedString {
                    withStyle(
                        style = ParagraphStyle(
                            textAlign = TextAlign.End,
                            textIndent = TextIndent.None
                        )
                    ) {
                        append(ayah.text)
                    }
                },
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.End,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            ayah.textTranslation?.let { translation ->
                Text(
                    text = translation,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            
            ayah.textTransliteration?.let { transliteration ->
                Text(
                    text = transliteration,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = "Ayet ${ayah.numberInSurah}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuranSettingsBottomSheet(
    state: QuranSettingsState,
    onEvent: (QuranJuzDetailScreenEvent) -> Unit,
    onDismiss: () -> Unit
) {
    if (state.showSettings) {
        ModalBottomSheet(
            onDismissRequest = onDismiss,
            dragHandle = { BottomSheetDefaults.DragHandle() },
            containerColor = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Ayarlar",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Çeviri Seçenekleri
                Text(
                    text = "Çeviri",
                    style = MaterialTheme.typography.titleMedium
                )
                
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                ) {
                    items(state.translationList) { translation ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onEvent(QuranJuzDetailScreenEvent.SetTranslation(translation.identifier))
                                }
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = translation.language,
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.weight(1f)
                            )
                            
                            if (state.selectedTranslation == translation.identifier) {
                                Icon(
                                    Icons.Default.Check,
                                    contentDescription = "Seçili",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Okuyucu Seçenekleri
                Text(
                    text = "Okuyucu",
                    style = MaterialTheme.typography.titleMedium
                )
                
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                ) {
                    itemsIndexed(state.reciterList) { index, reciter ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onEvent(QuranJuzDetailScreenEvent.SetReciter(reciter.identifier, index))
                                }
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = reciter.language,
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.weight(1f)
                            )
                            
                            if (state.selectedReciter == reciter.identifier) {
                                Icon(
                                    Icons.Default.Check,
                                    contentDescription = "Seçili",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Diğer ayarlar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Otomatik Oynatma",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.weight(1f)
                    )
                    
                    Switch(
                        checked = state.autoHidePlayer,
                        onCheckedChange = { onEvent(QuranJuzDetailScreenEvent.ToggleAutoHidePlayer) }
                    )
                }
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Sesleri Önbelleğe Al",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.weight(1f)
                    )
                    
                    Switch(
                        checked = state.shouldCacheAudio,
                        onCheckedChange = { onEvent(QuranJuzDetailScreenEvent.SetShouldCacheAudio(it)) }
                    )
                }
                
                Spacer(modifier = Modifier.height(100.dp))
            }
        }
    }
} 