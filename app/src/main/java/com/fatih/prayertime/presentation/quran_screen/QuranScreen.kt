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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.fatih.prayertime.R
import com.fatih.prayertime.data.remote.dto.qurandto.SurahInfo
import com.fatih.prayertime.domain.model.JuzInfo
import com.fatih.prayertime.util.composables.ErrorView
import com.fatih.prayertime.util.composables.TitleView
import com.fatih.prayertime.util.config.NavigationConfig.screens
import com.fatih.prayertime.util.extensions.navigateToScreen
import com.fatih.prayertime.util.extensions.toText
import com.fatih.prayertime.util.model.enums.PrayTimesString
import com.fatih.prayertime.util.model.state.QuranScreenState


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuranScreen(
    modifier: Modifier,
    navController: NavController,
    viewModel: QuranViewModel = hiltViewModel()
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
                    when (state.selectedTabIndex) {
                        0 -> SurahList (
                            surahList = state.surahList,
                            onSurahClick = { surahInfo ->
                                val route = screens.find{it.title == PrayTimesString.QURAN_DETAIL_SCREEN}!!.route.replace("{surahNumber}",surahInfo.number.toString())
                                navController.navigateToScreen(route)
                            }
                        )
                        1 -> JuzList (
                            juzInfoList = state.juzList,
                            onJuzClick = { juz ->
                                val route = screens.find{it.title == PrayTimesString.QURAN_JUZ_DETAIL_SCREEN}!!.route.replace("{juzNumber}", juz.juzNumber.toString())
                                navController.navigateToScreen(route)
                            }
                        )
                    }
                }
            }else{
                ErrorView(state.error?:"Error Occurred") {
                    viewModel.retry()
                }
            }
        }
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
                    text = stringResource(R.string.number_of_verses),
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
                    contentDescription = stringResource(R.string.listen),
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
        onClick = {}
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = stringResource(R.string.juz_number, juzInfo.juzNumber),
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
                    contentDescription = stringResource(R.string.listen),
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
