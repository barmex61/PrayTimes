package com.fatih.prayertime.presentation.favorites_screen

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.exyte.animatednavbar.utils.toPxf
import com.fatih.prayertime.R
import com.fatih.prayertime.data.local.entity.FavoritesEntity
import com.fatih.prayertime.util.composables.TitleView
import com.fatih.prayertime.util.config.NavigationConfig.screens
import com.fatih.prayertime.util.extensions.navigateToScreen
import com.fatih.prayertime.util.model.enums.FavoritesType
import kotlinx.coroutines.delay
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Composable
fun FavoritesScreen(
    modifier: Modifier,
    navController: NavController,
    viewModel: FavoritesViewModel = hiltViewModel()
) {
    val favorites by viewModel.favorites.collectAsState()
    val selectedType by viewModel.selectedType.collectAsState()
    var showDeleteDialog by remember { mutableStateOf(false) }
    var favoriteToDelete by remember { mutableStateOf<FavoritesEntity?>(null) }
    var deleteIconRotation by remember { mutableFloatStateOf(0f) }

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            AnimatedFilterChips(
                selectedType = selectedType,
                onTypeSelected = viewModel::setType
            )
        }

        if (favorites.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.no_favorites),
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            LazyColumn {
                items(
                    items = favorites,
                    key = { it.itemId }
                ) { favorite ->
                    val f = 75.dp.toPxf()
                    var isVisible by remember { mutableStateOf(true) }
                    val dismissState = rememberSwipeToDismissBoxState(
                        positionalThreshold = { f },
                        confirmValueChange = { dismissValue ->
                            if (dismissValue == SwipeToDismissBoxValue.StartToEnd) {
                                isVisible = false
                                true
                            } else false
                        }
                    )

                    AnimatedVisibility(
                        visible = isVisible,
                        enter = expandVertically() + fadeIn(),
                        exit = shrinkVertically() + fadeOut()
                    ) {
                        SwipeToDismissBox(
                            state = dismissState,
                            backgroundContent = {
                                val color by animateColorAsState(
                                    targetValue = when (dismissState.targetValue) {
                                        SwipeToDismissBoxValue.StartToEnd -> Color.Red.copy(alpha = 0.8f)
                                        else -> Color.Transparent
                                    },
                                    label = "background color animation"
                                )

                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(vertical = 8.dp, horizontal = 4.dp)
                                        .background(color, shape = RoundedCornerShape(32.dp)),
                                    contentAlignment = Alignment.CenterStart
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = stringResource(id = R.string.remove_from_favorites),
                                        tint = Color.White,
                                        modifier = Modifier
                                            .padding(start = 16.dp)
                                            .graphicsLayer(
                                                rotationZ = deleteIconRotation
                                            )
                                    )
                                }
                            },
                            enableDismissFromEndToStart = true,
                            content = {
                                FavoriteItem(
                                    favorite = favorite,
                                    onDeleteClick = {
                                        favoriteToDelete = favorite
                                        showDeleteDialog = true
                                        deleteIconRotation = 0f
                                    },
                                    onItemClick = {
                                        if (favorite.type == FavoritesType.DUA.name) {
                                            val subRoute = screens[7].route.replace("{duaId}","${favorite.duaId}")
                                            val route = subRoute.replace("{categoryId}","${favorite.duaCategoryId}")
                                            navController.navigateToScreen(route)
                                        }
                                        if(favorite.type == FavoritesType.HADIS.name){
                                            val encodedUrl = URLEncoder.encode(favorite.hadithCollectionPath, StandardCharsets.UTF_8.toString())
                                            val route = screens[5].route
                                                .replace("{collectionPath}",encodedUrl)
                                                .replace("{hadithSectionIndex}","${favorite.hadithSectionIndex}")
                                                .replace("{hadithIndex}","${favorite.hadithIndex}")
                                            navController.navigateToScreen(route)
                                        }
                                    }
                                )
                            }
                        )
                    }

                    LaunchedEffect(isVisible) {
                        if (!isVisible) {
                            delay(500)
                            viewModel.removeFromFavorites(favorite)
                        }
                    }
                }
            }
        }
    }

    if (showDeleteDialog) {

        AlertDialog(
            onDismissRequest = {
                showDeleteDialog = false
                favoriteToDelete = null
            },
            title = { Text(stringResource(R.string.remove_from_favorites)) },
            text = { Text(stringResource(R.string.are_you_sure_delete_favorite)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        favoriteToDelete?.let { viewModel.removeFromFavorites(it) }
                        showDeleteDialog = false
                        favoriteToDelete = null
                    }
                ) {
                    Text("Evet")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        favoriteToDelete = null
                    }
                ) {
                    Text("Hayır")
                }
            }
        )
    }
    TitleView(stringResource(R.string.favorites))
}

@Composable
private fun AnimatedFilterChips(
    selectedType: String,
    onTypeSelected: (String) -> Unit
) {
    val types = listOf(
        FavoritesType.DUA.name to R.string.prayers,
        FavoritesType.HADIS.name to R.string.hadiths
    )

    types.forEach { (type, stringRes) ->
        val isSelected = selectedType == type
        val scale by animateFloatAsState(
            targetValue = if (isSelected) 1.1f else 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            ),
            label = ""
        )

        FilterChip(
            selected = isSelected,
            onClick = { onTypeSelected(type) },
            label = {
                Text(modifier = Modifier.padding(5.dp),
                    text =stringResource(stringRes),
                    color = if(selectedType == type) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onPrimaryContainer)
            },
            modifier = Modifier
                .padding(horizontal = 4.dp)
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                }
        )
    }
}

@Composable
fun FavoriteItem(
    favorite: FavoritesEntity,
    onDeleteClick: () -> Unit,
    onItemClick: () -> Unit
) {
    val deleteIconRotation by remember { mutableFloatStateOf(0f) }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        onClick = onItemClick,
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = favorite.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = favorite.content,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
                favorite.latin?.let { latinContent ->
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = latinContent,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis
                    )
                }

            }
            IconButton(
                onClick = onDeleteClick,
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = stringResource(id = R.string.remove_from_favorites),
                    tint = MaterialTheme.colorScheme.onError,
                    modifier = Modifier.graphicsLayer(
                        rotationZ = deleteIconRotation
                    ).padding(start = 16.dp)
                )
            }
        }
    }
}