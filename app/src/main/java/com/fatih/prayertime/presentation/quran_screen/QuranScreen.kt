package com.fatih.prayertime.presentation.quran_screen

import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.fatih.prayertime.data.remote.dto.qurandto.SurahInfo
import com.fatih.prayertime.domain.model.JuzInfo
import com.fatih.prayertime.util.composables.TitleView


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuranScreen(
    modifier: Modifier,
    navController: NavController,
    viewModel: QuranViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            QuranTopBar(scrollBehavior = scrollBehavior)
            QuranTabRow(
                selectedTabIndex = state.selectedTabIndex,
                onTabSelected = viewModel::onTabSelected
            )

            Box(modifier = Modifier.weight(1f)) {
                when (state.selectedTabIndex) {
                    0 -> SurahList (
                        surahList = state.surahList,
                        onSurahClick = { surahName ->
                            // TODO: Navigate to detail
                        }
                    )
                    1 -> JuzList (
                        juzInfoList = state.juzList,
                        onJuzClick = { juz ->
                            // TODO: Navigate to detail
                        }
                    )
                }
            }
        }

        QuranFab(
            modifier = modifier,
            onReciterSelected = viewModel::onReciterSelected,
            onTranslationSelected = viewModel::onTranslationSelected,
            onPronunciationSelected = viewModel::onPronunciationSelected
        )
    }

    TitleView("Kuran-ı Kerim")
}

@Composable
fun SurahList(
    surahList: List<SurahInfo>,
    onSurahClick: (SurahInfo) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(surahList) { surah ->
            SurahCard(surah = surah, onClick = { onSurahClick(surah) })
        }
    }
}

@Composable
fun JuzList(
    juzInfoList: List<JuzInfo>,
    onJuzClick: (JuzInfo) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(juzInfoList) { juzInfo ->
            JuzCard(juzInfo = juzInfo, onClick = { onJuzClick(juzInfo) })
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SurahCard(
    surah: SurahInfo,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(88.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = surah.number.toString(),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontWeight = FontWeight.Bold
                    )
                }
                Column {
                    Text(
                        text = surah.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text ="surah.arabicName",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "$ Ayet",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                IconButton(
                    onClick = { /* Dinleme fonksiyonu */ },
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Icon(
                        Icons.Default.PlayArrow,
                        contentDescription = "Dinle",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JuzCard(
    juzInfo: JuzInfo,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = juzInfo.juzName,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontWeight = FontWeight.Bold
                    )
                }
                Column {
                    Text(
                        text = "${juzInfo.juzNumber}. Cüz",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = juzInfo.verseRange,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            IconButton(
                onClick = { /* Dinleme fonksiyonu */ },
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer)
            ) {
                Icon(
                    Icons.Default.PlayArrow,
                    contentDescription = "Dinle",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun QuranTopBar(
    scrollBehavior: TopAppBarScrollBehavior
) {
    TopAppBar(
        title = { Text("Kuran-ı Kerim") },
        colors = TopAppBarDefaults.largeTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            scrolledContainerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        scrollBehavior = scrollBehavior
    )
}

@Composable
private fun QuranTabRow(
    selectedTabIndex: Int,
    onTabSelected: (Int) -> Unit
) {
    val tabs = listOf("Sureler", "Cüzler")
    TabRow(
        selectedTabIndex = selectedTabIndex,
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        indicator = { tabPositions ->
            Box(
                modifier = Modifier
                    .tabIndicatorOffset(tabPositions[selectedTabIndex])
                    .height(3.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(3.dp)
                    )
            )
        }
    ) {
        tabs.forEachIndexed { index, title ->
            Tab(
                selected = selectedTabIndex == index,
                onClick = { onTabSelected(index) },
                text = { 
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal
                    )
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BoxScope.QuranFab(
    modifier: Modifier,
    onReciterSelected: (String) -> Unit,
    onTranslationSelected: (String) -> Unit,
    onPronunciationSelected: (String) -> Unit
) {
    var openMenu by remember { mutableStateOf(false) }
    var expandedReciter by remember { mutableStateOf(false) }
    var expandedTranslation by remember { mutableStateOf(false) }
    var expandedPronunciation by remember { mutableStateOf(false) }
    
    Box (modifier = modifier.align(Alignment.BottomEnd)){

        FloatingActionButton(
            onClick = {
                openMenu = !openMenu
            },
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)

        ) {
            Icon(
                imageVector = Icons.Default.Menu ,
                contentDescription = "Seçenekler"
            )
        }

        AnimatedVisibility(
            visible = openMenu,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Card(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(bottom = 80.dp, end = 16.dp)
                    .width(300.dp),
                elevation = CardDefaults.cardElevation(8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Ayarlar",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )

                    // Okuyucu Seçimi
                    ExposedDropdownMenuBox(
                        expanded = expandedReciter,
                        onExpandedChange = { expandedReciter = it }
                    ) {
                        OutlinedTextField(
                            value = "Okuyucu Seç",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Müezzin") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedReciter) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = expandedReciter,
                            onDismissRequest = { expandedReciter = false }
                        ) {
                            getDummyReciters().forEach { reciter ->
                                DropdownMenuItem(
                                    text = { Text(reciter) },
                                    onClick = {
                                        onReciterSelected(reciter)
                                        expandedReciter = false
                                    }
                                )
                            }
                        }
                    }

                    // Çeviri Seçimi
                    ExposedDropdownMenuBox(
                        expanded = expandedTranslation,
                        onExpandedChange = { expandedTranslation = it }
                    ) {
                        OutlinedTextField(
                            value = "Çeviri Seç",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Meal Dili") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedTranslation) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = expandedTranslation,
                            onDismissRequest = { expandedTranslation = false }
                        ) {
                            getDummyTranslations().forEach { translation ->
                                DropdownMenuItem(
                                    text = { Text(translation) },
                                    onClick = {
                                        onTranslationSelected(translation)
                                        expandedTranslation = false
                                    }
                                )
                            }
                        }
                    }

                    // Okunuş Seçimi
                    ExposedDropdownMenuBox(
                        expanded = expandedPronunciation,
                        onExpandedChange = { expandedPronunciation = it }
                    ) {
                        OutlinedTextField(
                            value = "Okunuş Seç",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Okunuş Dili") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedPronunciation) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = expandedPronunciation,
                            onDismissRequest = { expandedPronunciation = false }
                        ) {
                            getDummyPronunciations().forEach { pronunciation ->
                                DropdownMenuItem(
                                    text = { Text(pronunciation) },
                                    onClick = {
                                        onPronunciationSelected(pronunciation)
                                        expandedPronunciation = false
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

    }
}

private fun getDummyReciters() = listOf(
    "Abdul Basit",
    "Mishari Rashid",
    "Saud Al-Shuraim",
    "Abdurrahman Al-Sudais"
)

private fun getDummyTranslations() = listOf(
    "Diyanet İşleri",
    "Elmalılı Hamdi Yazır",
    "Ali Bulaç",
    "Süleymaniye Vakfı"
)

private fun getDummyPronunciations() = listOf(
    "Türkçe",
    "Arapça",
    "İngilizce"
) 