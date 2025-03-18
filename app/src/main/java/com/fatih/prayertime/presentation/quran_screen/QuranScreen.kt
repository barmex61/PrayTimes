package com.fatih.prayertime.presentation.quran_screen

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.fatih.prayertime.data.remote.dto.qurandto.SurahInfo
import com.fatih.prayertime.domain.model.JuzInfo
import com.fatih.prayertime.util.composables.ErrorView
import com.fatih.prayertime.util.composables.TitleView
import com.fatih.prayertime.util.config.NavigationConfig.screens
import com.fatih.prayertime.util.extensions.navigateToScreen
import com.fatih.prayertime.util.extensions.toText
import com.fatih.prayertime.util.model.state.QuranScreenState


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuranScreen(
    modifier: Modifier,
    navController: NavController,
    viewModel: QuranViewModel
) {
    val state by viewModel.quranScreenState.collectAsStateWithLifecycle()
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
            if (state.error == null){
                Box(modifier = Modifier.weight(1f)) {
                    var isNavigating = remember { false }
                    when (state.selectedTabIndex) {
                        0 -> SurahList (
                            surahList = state.surahList,
                            onSurahClick = { surahInfo ->
                                println("onsurahsclick")
                                if (!isNavigating){
                                    isNavigating = true
                                    viewModel.getSelectedSurah(surahInfo.number) {
                                        println("naviagete")
                                        navController.navigateToScreen(screens[15].route)
                                        isNavigating = false
                                    }
                                }

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
            }else{
                ErrorView(state.error?:"Error Occurred") {
                    viewModel.loadJuzList()
                    viewModel.loadSurahList()
                    viewModel.loadTranslationList()
                    viewModel.loadAudioList()
                }
            }
        }

        QuranFab(
            modifier = modifier,
            onReciterSelected = viewModel::onReciterSelected,
            onTranslationSelected = viewModel::onTranslationSelected,
            onTransliterationSelected = viewModel::onTransliterationSelected,
            state = state
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
            SurahCard(surahInfo = surah, onClick = { onSurahClick(surah) })
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
    surahInfo: SurahInfo,
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
                .padding(horizontal = 32.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = surahInfo.number.toString(),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                fontWeight = FontWeight.Bold
            )
            Column (horizontalAlignment = Alignment.Start){
                Text(
                    text = surahInfo.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = surahInfo.turkishName?:"",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Column (modifier = Modifier.padding(start = 16.dp), horizontalAlignment = Alignment.CenterHorizontally){
                Text(
                    text = "Ayet Sayısı",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = surahInfo.numberOfAyahs.toString(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
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

            Text(
                text = "${juzInfo.juzNumber}.Cüz",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = juzInfo.juzName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

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

    CenterAlignedTopAppBar(
        title = {

        },
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
    onTransliterationSelected: (String) -> Unit,
    state : QuranScreenState
) {
    var showBottomSheet by remember { mutableStateOf(false) }
    var expandedReciter by remember { mutableStateOf(false) }
    var expandedTranslation by remember { mutableStateOf(false) }
    var expandedPronunciation by remember { mutableStateOf(false) }
    val translationList = remember(state) { state.translationList }
    val reciterList = remember(state) { state.reciterList}
    val pronunciationList = remember(state) { state.transliterationList}
    val selectedTranslation = remember(state) { state.selectedTranslation }
    val selectedPronunciation = remember(state) { state.selectedTransliteration}
    val selectedReciter = remember(state) { state.selectedReciter}
    println(expandedReciter)
    
    val rotation by animateFloatAsState(
        targetValue = if (showBottomSheet) 45f else 0f,
        animationSpec = tween(300),
        label = "rotation"
    )
    
    val scale by animateFloatAsState(
        targetValue = if (showBottomSheet) 1.1f else 1f,
        animationSpec = tween(300),
        label = "scale"
    )
    
    Box(modifier = modifier.align(Alignment.BottomEnd)) {
        FloatingActionButton(
            onClick = { showBottomSheet = true },
            containerColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 1f),
            contentColor = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
                .graphicsLayer {
                    rotationZ = rotation
                    scaleX = scale
                    scaleY = scale
                }
        ) {
            Icon(
                imageVector = Icons.Default.Menu,
                contentDescription = "Seçenekler"
            )
        }
    }

    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = rememberModalBottomSheetState(),
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            dragHandle = { BottomSheetDefaults.DragHandle() },
            tonalElevation = 10.dp,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Ayarlar",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                // Okuyucu Seçimi
                ExposedDropdownMenuBox(
                    modifier = Modifier.clickable{
                        println("click")
                    },
                    expanded = expandedReciter,
                    onExpandedChange = {
                        println("expand")
                        expandedReciter = it }
                ) {
                    OutlinedTextField(
                        value = selectedReciter,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Seslendiren") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedReciter) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = expandedReciter,
                        onDismissRequest = { expandedReciter = false }
                    ) {
                        reciterList.forEach { reciter ->
                            DropdownMenuItem(
                                text = { Text(reciter.toText()) },
                                onClick = {
                                    onReciterSelected(reciter.toText())
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
                            value = selectedTranslation,
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
                            translationList.forEach { translation ->
                                DropdownMenuItem(
                                    text = { Text(translation.toText()) },
                                    onClick = {
                                        onTranslationSelected(translation.toText())
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
                        value = selectedPronunciation,
                        onValueChange = {

                        },
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

                        pronunciationList.keys.forEach { transliteration ->
                            DropdownMenuItem(
                                text = { Text(transliteration) },
                                onClick = {
                                    onTransliterationSelected(transliteration)
                                    expandedPronunciation = false
                                }
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(50.dp))
            }
        }
    }
}
