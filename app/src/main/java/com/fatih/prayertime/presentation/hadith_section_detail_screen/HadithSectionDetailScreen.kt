package com.fatih.prayertime.presentation.hadith_section_detail_screen

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.InfiniteTransition
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fatih.prayertime.data.remote.dto.hadithdto.Hadith
import com.fatih.prayertime.presentation.hadith_collections_screen.HadithCollectionViewModel
import com.fatih.prayertime.util.TitleView
import kotlin.random.Random

@Composable
fun HadithSectionDetailScreen(
    bottomPaddingValues: Dp,
    hadithCollectionViewModel: HadithCollectionViewModel
) {
    val selectedHadithSection by hadithCollectionViewModel.selectedHadithSection.collectAsState()
    val selectedIndex by hadithCollectionViewModel.selectedHadithIndex.collectAsState()
    var direction by remember { mutableIntStateOf(1) }
    val infiniteTransition = rememberInfiniteTransition()

    var showAllHadiths by remember { mutableStateOf(false) } // "Tüm Hadisleri Göster" seçeneği için durum
    val fabButtonTransition = infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -200f,
        animationSpec = infiniteRepeatable(tween(6000), repeatMode = RepeatMode.Reverse)
    )
    val fabButtonRotate = animateFloatAsState(
        targetValue = if (showAllHadiths) 360f else 0f,
    )


    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = bottomPaddingValues, top = 12.dp)
    ) {
        selectedHadithSection?:return@Box
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            selectedHadithSection?.let { hadithSection ->
                HadithSectionCard(hadithSection.section)
            }

            Spacer(modifier = Modifier.height(16.dp))

            PageNavigationRow(
                selectedIndex = selectedIndex,
                totalPages = selectedHadithSection?.hadithCount ?: 0,
                onPageChange = { hadithCollectionViewModel.updateSelectedHadithIndex(it) },
                changeDirection = { selectedDirection -> direction = selectedDirection },
                infiniteTransition = infiniteTransition
            )
            Spacer(modifier = Modifier.size(16.dp))


            AnimatedVisibility(
                visible = showAllHadiths,
                enter = slideInVertically(tween(1000)) { it },
                exit = slideOutVertically(tween(1000)) { it }
            ) {
                LazyColumn  {
                    itemsIndexed(selectedHadithSection!!.hadithList){ index,hadith->
                        HadithDetailSection(hadith,infiniteTransition)
                        if (index < selectedHadithSection!!.hadithList.size - 1) {
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }
                }
            }

            if(!showAllHadiths){
                val selectedHadith = remember { selectedHadithSection!!.hadithList[selectedIndex] }
                LaunchedEffect(key1 = selectedHadith) {
                    hadithCollectionViewModel.checkIsFavorite(selectedHadith.hadithnumber.toInt())
                }
                AnimatedContent(
                    targetState = selectedHadith,
                    transitionSpec = {
                        if (direction == 1) {
                            fadeIn(tween(1000)) + slideInHorizontally(tween(1000)) { width -> +width } togetherWith
                                    fadeOut(tween(1000)) + slideOutHorizontally(tween(1000)) { width -> -width }
                        } else {
                            fadeIn(tween(1000)) + slideInHorizontally(tween(1000)) { width -> -width } togetherWith
                                    fadeOut(tween(1000)) + slideOutHorizontally(tween(1000)) { width -> +width }
                        }
                    }
                ) {
                    LazyColumn {
                        item {
                            HadithDetailSection(it,infiniteTransition)
                        }
                    }
                }

            }
        }
        FloatingActionButton(
            onClick = {
                if (!showAllHadiths){
                    hadithCollectionViewModel.toggleFavorite(selectedHadithSection!!.hadithList[selectedIndex])
                }
            },

            modifier = Modifier
                .padding(bottom = 64.dp)
                .align(Alignment.BottomEnd).graphicsLayer {
                    translationY = fabButtonTransition.value
                    rotationX = fabButtonRotate.value
                }, // Sağ alt köşe
        ) {
            val isFavorite by hadithCollectionViewModel.isFavorite.collectAsState()
            Icon(
                imageVector = Icons.Default.Favorite,
                contentDescription = if (showAllHadiths) "Close All Hadiths" else "Show All Hadiths",
                tint = if (!showAllHadiths && isFavorite) Color.Red else Color.Gray
            )
        }
        FloatingActionButton(
            onClick = { showAllHadiths = !showAllHadiths },
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.BottomEnd).graphicsLayer {
                    translationY = fabButtonTransition.value
                    rotationX = fabButtonRotate.value
                } // Sağ alt köşe
        ) {
            Icon(
                imageVector = if (showAllHadiths) Icons.Default.Close else Icons.Default.List,
                contentDescription = if (showAllHadiths) "Close All Hadiths" else "Show All Hadiths",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
    TitleView("Hadith Details")
}

@Composable
fun HadithSectionCard(sectionTitle: String?) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(10.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            text = sectionTitle ?: "",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
fun PageNavigationRow(
    selectedIndex: Int,
    totalPages: Int,
    onPageChange: (Int) -> Unit,
    changeDirection : (Int) -> Unit,
    infiniteTransition: InfiniteTransition
) {
    var expanded by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        NavigationButton(
            icon = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Previous Page",
            onClick = {
                changeDirection(-1)
                onPageChange((selectedIndex - 1).coerceAtLeast(0))
                      },
            modifier = Modifier.weight(2f),
            infiniteTransition = infiniteTransition,
            direction = -1
        )

        PageSelectionDropdown(
            selectedIndex = selectedIndex,
            totalPages = totalPages,
            onPageSelected = {
                onPageChange(it)
                expanded = false
            },
            expanded = expanded,
            onExpandedChange = { expanded = it },
            modifier = Modifier
                .padding(start = 10.dp, end = 10.dp)
                .weight(5f),
            changeDirection = changeDirection
        )

        NavigationButton(
            icon = Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = "Next Page",
            onClick = {
                changeDirection(1)
                onPageChange((selectedIndex + 1).coerceAtMost(totalPages - 1))
                      },
            modifier = Modifier.weight(2f),
            infiniteTransition = infiniteTransition,
            direction = 1
        )
    }
}

@Composable
fun NavigationButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    infiniteTransition: InfiniteTransition,
    direction : Int
) {
    val scale = infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            tween(2000),
            repeatMode = RepeatMode.Reverse
        )
    )
    val transition = infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 20f,
        animationSpec = infiniteRepeatable(
            tween(2000),
            repeatMode = RepeatMode.Reverse,
        )
    )
    IconButton(
        onClick = onClick,
        modifier = modifier.graphicsLayer {
            scaleX = scale.value
            scaleY = scale.value
            translationX = transition.value * direction
        }
    ) {
        Icon(
            modifier = Modifier
                .background(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(20.dp)
                )
                .padding(8.dp)
              ,
            imageVector = icon,
            contentDescription = contentDescription,
            tint = MaterialTheme.colorScheme.primary
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PageSelectionDropdown(
    selectedIndex: Int,
    totalPages: Int,
    onPageSelected: (Int) -> Unit,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    changeDirection: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = onExpandedChange,
        modifier = modifier
    ) {
        OutlinedTextField(
            value = "${selectedIndex + 1} / $totalPages",
            onValueChange = {},
            readOnly = true,
            modifier = Modifier
                .menuAnchor(type = MenuAnchorType.PrimaryEditable)
                .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(8.dp))
                .height(50.dp)
                .fillMaxWidth(),
            textStyle = TextStyle(
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            ),
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Açılır Menü",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { onExpandedChange(false) },
            modifier = Modifier.background(MaterialTheme.colorScheme.surface, RoundedCornerShape(10.dp))
        ) {
            (0 until totalPages).forEach { index ->
                DropdownMenuItem(
                    text = {
                        Text(
                            "${index + 1}",
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(8.dp)
                        )
                    },
                    onClick = {
                        onPageSelected(index)
                        val direction = if (index >= selectedIndex) 1 else -1
                        changeDirection(direction)
                              },
                    modifier = Modifier.background(MaterialTheme.colorScheme.surface)
                )
            }
        }
    }
}

@Composable
fun HadithDetailSection(hadith: Hadith,infiniteTransition: InfiniteTransition) {
    val translation = infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = if (Random.nextBoolean()) Random.nextFloat() * 4f + 1f else Random.nextFloat() * -4f - 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000),
            repeatMode = RepeatMode.Reverse
        )
    )

    Card(
        modifier = Modifier.fillMaxWidth().padding(16.dp).graphicsLayer {
            translationX = translation.value.dp.toPx()
            translationY = translation.value.dp.toPx()
        },
        elevation = CardDefaults.cardElevation(6.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        onClick = {

        }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = hadith.text,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                textAlign = TextAlign.Justify
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Hadis Referans Bilgileri
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val hadithNo= try {
                    hadith.hadithnumber.toInt().toString()
                }catch (e:Exception){
                    hadith.toString()
                }
                val arabicNo = try {
                    hadith.arabicnumber.toInt().toString()
                }catch (e:Exception){
                    hadith.toString()
                }
                Text(
                    text = "Hadith No: $hadithNo",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Arabic No: $arabicNo",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Reference: Book ${hadith.reference.book}, Hadith ${hadith.reference.hadith}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.align(Alignment.End)
            )
        }
    }
}
