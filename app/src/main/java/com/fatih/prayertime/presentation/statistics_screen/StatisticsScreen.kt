package com.fatih.prayertime.presentation.statistics_screen

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.fatih.prayertime.data.local.entity.PrayerStatisticsEntity
import com.fatih.prayertime.util.composables.TitleView

@Composable
fun StatisticsScreen(
    bottomPaddingValue: Dp,
    viewModel: StatisticsViewModel = hiltViewModel()
) {
    val statistics by viewModel.statistics.collectAsState()
    val completedCount by viewModel.completedPrayersCount.collectAsState()
    val onTimeCount by viewModel.onTimePrayersCount.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = bottomPaddingValue)
    ) {
        // İstatistik kartları
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatisticsCard(
                title = "Toplam Kılınan",
                value = completedCount.toString(),
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(16.dp))
            StatisticsCard(
                title = "Vaktinde Kılınan",
                value = onTimeCount.toString(),
                modifier = Modifier.weight(1f)
            )
        }

        // İstatistik grafiği
        StatisticsChart(
            statistics = statistics,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(16.dp)
        )

        // Günlük istatistikler
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(statistics) { statistic ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = statistic.prayerName,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = statistic.date,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (statistic.isCompleted) "Kılındı" else "Kılınmadı",
                                color = if (statistic.isCompleted) 
                                    MaterialTheme.colorScheme.primary 
                                else 
                                    MaterialTheme.colorScheme.error
                            )
                            if (statistic.isCompleted && statistic.isOnTime) {
                                Text(
                                    text = "Vaktinde",
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
    TitleView("İstatistikler")
}

@Composable
fun StatisticsChart(
    statistics: List<PrayerStatisticsEntity>,
    modifier: Modifier = Modifier
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondary

    Canvas(modifier = modifier.fillMaxSize()) {
        if (statistics.isEmpty()) return@Canvas

        val width = size.width
        val height = size.height
        val xStep = width / (statistics.size - 1)
        val maxCompleted = statistics.size
        val yStep = height / maxCompleted

        // Çizgi grafiği için path
        val completedPath = Path()
        val onTimePath = Path()

        statistics.forEachIndexed { index, stat ->
            val x = index * xStep
            val completedY = height - (if (stat.isCompleted) 1 else 0) * yStep
            val onTimeY = height - (if (stat.isOnTime) 1 else 0) * yStep

            if (index == 0) {
                completedPath.moveTo(x, completedY)
                onTimePath.moveTo(x, onTimeY)
            } else {
                completedPath.lineTo(x, completedY)
                onTimePath.lineTo(x, onTimeY)
            }

            // Nokta çizimi
            drawCircle(
                color = primaryColor,
                radius = 4f,
                center = Offset(x, completedY)
            )
            if (stat.isOnTime) {
                drawCircle(
                    color = secondaryColor,
                    radius = 4f,
                    center = Offset(x, onTimeY)
                )
            }
        }

        // Çizgileri çiz
        drawPath(
            path = completedPath,
            color = primaryColor,
            style = Stroke(width = 2f)
        )
        drawPath(
            path = onTimePath,
            color = secondaryColor,
            style = Stroke(width = 2f)
        )
    }
}

@Composable
fun StatisticsCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )
        }
    }
}