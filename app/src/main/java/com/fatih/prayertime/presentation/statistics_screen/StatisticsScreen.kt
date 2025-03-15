package com.fatih.prayertime.presentation.statistics_screen

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.material3.ProgressIndicatorDefaults.drawStopIndicator
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.fatih.prayertime.data.local.entity.PrayerStatisticsEntity
import com.fatih.prayertime.util.composables.TitleView
import kotlin.collections.forEachIndexed
import com.fatih.prayertime.R
import com.fatih.prayertime.util.model.state.StatisticsState
import org.threeten.bp.LocalDate
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(
    modifier: Modifier,
    viewModel: StatisticsViewModel = hiltViewModel()
) {
    println("statistics screen")
    val statisticsState by viewModel.statisticsState.collectAsState()
    val context = LocalContext.current
    val options = remember {
        listOf(
            context.getString(R.string.last_7_days),
            context.getString(R.string.last_30_days),
            context.getString(R.string.last_3_months),
            context.getString(R.string.all_times)
        )
    }

    val title = remember { context.getString(R.string.select_date_range) }
    var showDatePicker by remember { mutableStateOf(false) }
    var selectedDateRangeString by rememberSaveable { mutableStateOf(options[0]) }
    val scrollState = rememberScrollState()
    val longestStreak by viewModel.longestSeries.collectAsState()
    val completedPercentageList = viewModel.completePercentageList.collectAsState()
    LaunchedEffect(selectedDateRangeString) {
        val selectedDateRange = when(selectedDateRangeString){
            options[0] -> LocalDate.now().minusWeeks(1)..LocalDate.now()
            options[1] -> LocalDate.now().minusMonths(1)..LocalDate.now()
            options[2] -> LocalDate.now().minusMonths(3)..LocalDate.now()
            else -> LocalDate.now().minusYears(1)..LocalDate.now()
        }
        viewModel.updateDateRange(selectedDateRange)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        DateRangeCard(
            selectedDateRange = selectedDateRangeString,
            onDateRangeClick = { showDatePicker = true }
        )

        SuccessCard(
            statisticsState = statisticsState,
            longestStreak = longestStreak // Bu değer viewModel'dan gelmeli
        )

        StatisticsSummaryRow(statisticsState = statisticsState)
        Spacer(modifier = Modifier.height(8.dp))
        StatisticsChart(statistics = statisticsState.statistics)
        StatisticsDetailsCard(statisticsState = statisticsState)
        DetailedPrayerStatistics(completedPercentageList.value)
        Spacer(modifier = Modifier.height(8.dp))
    }

    if (showDatePicker) {
        DateRangePickerDialog(
            selectedDateRange = selectedDateRangeString,
            onDateRangeSelected = { range ->
                selectedDateRangeString = range
                showDatePicker = false
            },
            onDismissRequest = { showDatePicker = false },
            title = title,
            options = options
        )
    }

    TitleView("İstatistikler")
}

@Composable
fun DateRangeCard(selectedDateRange: String, onDateRangeClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = selectedDateRange,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            FilledTonalIconButton(onClick = onDateRangeClick) {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = "Tarih Seç"
                )
            }
        }
    }
}

@Composable
fun StatisticsSummaryRow(statisticsState: StatisticsState) {
    val completedText = stringResource(R.string.completed_pray)
    val missedText = stringResource(R.string.missed_pray)
    val completedPrayer by animateIntAsState(
        targetValue = statisticsState.completedPrayers,
        animationSpec = tween(2000)
    )
    val missedPrayer by animateIntAsState(
        targetValue = statisticsState.missedPrayers,
        animationSpec = tween(2000)
    )
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        StatisticsCard(
            title = completedText,
            value =  completedPrayer,
            modifier = Modifier.weight(1f)
        )
        StatisticsCard(
            title = missedText,
            value = missedPrayer,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun StatisticsDetailsCard(statisticsState: StatisticsState) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            StatisticsDetailRow(label = stringResource(R.string.start_date), value = statisticsState.startDate)
            StatisticsDetailRow(label = stringResource(R.string.end_date), value = statisticsState.endDate)
            StatisticsDetailRow(label = stringResource(R.string.total_prayer), value = statisticsState.totalPrayers.toString())
        }
    }
}

@Composable
fun StatisticsDetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}

@Composable
fun DateRangePickerDialog(
    selectedDateRange: String,
    onDateRangeSelected: (String) -> Unit,
    onDismissRequest: () -> Unit,
    title: String,
    options : List<String>
) {

    val (selectedOption, onOptionSelected) = rememberSaveable { mutableStateOf(selectedDateRange) }

    AlertDialog(
        containerColor = MaterialTheme.colorScheme.secondaryContainer,
        onDismissRequest = onDismissRequest,
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            Column {
                options.forEach { range ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onOptionSelected(range)
                                onDateRangeSelected(range)
                            }
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (range == selectedOption),
                            onClick = {
                                onOptionSelected(range)
                                onDateRangeSelected(range)
                            }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = range,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text("İptal")
            }
        }
    )
}

@Composable
fun StatisticsChart(
    statistics: List<PrayerStatisticsEntity>,
) {
    if (statistics.isEmpty()) return

    val primaryColor = MaterialTheme.colorScheme.primary
    val onPrimaryContainer = MaterialTheme.colorScheme.onPrimaryContainer
    val onError = MaterialTheme.colorScheme.onError
    val dailyPrayerPairs = remember(statistics) {
        statistics.groupBy { it.date }
            .map { (date, prayerStatistics) ->
                date to prayerStatistics.count { it.isCompleted }
            }
            .sortedBy { it.first }
    }
    val dateList = dailyPrayerPairs.map { it.first }
    val completedPrayerList: List<Number> = dailyPrayerPairs.map { it.second }
    val notCompletedPrayerList: List<Int> = completedPrayerList.map { 5 - it.toInt() }

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = CardDefaults.elevatedShape,
        modifier = Modifier
            .fillMaxWidth()
            .requiredHeight(400.dp)
            .padding(8.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize().padding(top = 16.dp, end = 16.dp, bottom = 16.dp)) {
            val scrollState = rememberScrollState()
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .horizontalScroll(scrollState)
            ) {

                Canvas(
                    modifier = Modifier.width((dateList.size * 85).dp)
                        .fillMaxHeight()
                ) {

                    val width = size.width
                    val height = size.height
                    val padding = 100f
                    val chartWidth = width - (padding * 2)
                    val chartHeight = height - (padding * 2)
                    val arrowOffset = 95f // Okun yukarı kaydırılma miktarı
                    // Sütunlar ve değerler
                    val columnWidth = 70f
                    val spacing = 80f // Tarihler arası boşluk

                    // Y ekseni değerleri (0-5)
                    for (i in 0..5) {
                        val y = height - padding - (chartHeight * i / 5)
                        drawLine(
                            color = primaryColor.copy(alpha = 0.3f),
                            start = Offset(padding, y),
                            end = Offset(width - padding, y),
                            strokeWidth = 5f
                        )
                        drawContext.canvas.nativeCanvas.apply {
                            drawText(
                                i.toString(),
                                padding - 45f,
                                y + 12f,
                                android.graphics.Paint().apply {
                                    color = onPrimaryContainer.toArgb()
                                    textSize = 40f
                                }
                            )
                        }
                    }

                    dateList.forEachIndexed { index, date ->
                        val x = padding + 40f+ (index * (columnWidth * 2 + spacing))

                        // Kılınan namazlar (yeşil)
                        val completedHeight = chartHeight * (completedPrayerList[index].toFloat().coerceAtLeast(0.03f) / 5)
                        drawRect(
                            color = primaryColor,
                            topLeft = Offset(x, height - padding - completedHeight),
                            size = Size(columnWidth, completedHeight)
                        )

                        // Kaçırılan namazlar (kırmızı)
                        val notCompletedHeight = chartHeight * (notCompletedPrayerList[index].toFloat().coerceAtLeast(0.03f) / 5)
                        drawRect(
                            color = onError,
                            topLeft = Offset(x + columnWidth, height - padding - notCompletedHeight),
                            size = Size(columnWidth, notCompletedHeight)
                        )

                        // Değerleri göster
                        drawContext.canvas.nativeCanvas.apply {
                            // Kılınan namaz sayısı
                            drawText(
                                completedPrayerList[index].toString(),
                                x + columnWidth / 2 - 12f,
                                height - padding - completedHeight - 16f,
                                android.graphics.Paint().apply {
                                    color = primaryColor.toArgb()
                                    textSize = 36f
                                    isFakeBoldText = true
                                }
                            )
                            // Kaçırılan namaz sayısı
                            drawText(
                                notCompletedPrayerList[index].toString(),
                                x + columnWidth * 1.5f - 12f,
                                height - padding - notCompletedHeight - 16f,
                                android.graphics.Paint().apply {
                                    color = onError.toArgb()
                                    textSize = 36f
                                    isFakeBoldText = true
                                }
                            )
                            // Tarih
                            val dateText = date.substring(0, 5)
                            drawText(
                                dateText,
                                x + columnWidth - 54f,
                                height - padding + 45f,
                                android.graphics.Paint().apply {
                                    color = onPrimaryContainer.toArgb()
                                    textSize = 40f
                                    isFakeBoldText = true
                                }
                            )
                        }
                    }

                    // Y ekseni çizgisi ve oku
                    drawLine(
                        color = onPrimaryContainer,
                        start = Offset(padding, padding -arrowOffset),
                        end = Offset(padding, height - padding),
                        strokeWidth = 5f
                    )
                    // Y ekseni oku
                    val arrowSize = 30f
                    val arrowPath = Path().apply {
                        moveTo(padding, padding - arrowOffset)
                        lineTo(padding - arrowSize, padding + arrowSize - arrowOffset)
                        lineTo(padding + arrowSize, padding + arrowSize - arrowOffset)
                        close()
                    }
                    drawPath(
                        path = arrowPath,
                        color = onPrimaryContainer
                    )

                    // X ekseni çizgisi ve oku
                    drawLine(
                        color = onPrimaryContainer,
                        start = Offset(padding, height - padding),
                        end = Offset(width - padding, height - padding),
                        strokeWidth = 5f
                    )
                    // X ekseni oku
                    val xArrowPath = Path().apply {
                        moveTo(width - padding, height - padding)
                        lineTo(width - padding - arrowSize, height - padding - arrowSize)
                        lineTo(width - padding - arrowSize, height - padding + arrowSize)
                        close()
                    }
                    drawPath(
                        path = xArrowPath,
                        color = onPrimaryContainer
                    )


                }
            }

            // Legend
            Row(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(horizontal = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(18.dp)
                            .background(primaryColor)
                    )
                    Text(
                        text = stringResource(R.string.completed_pray),
                        style = MaterialTheme.typography.titleSmall,
                        color = primaryColor
                    )
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(18.dp)
                            .background(onError)
                    )
                    Text(
                        text = stringResource(R.string.missed_pray),
                        style = MaterialTheme.typography.titleSmall,
                        color = onError
                    )
                }
            }
        }
    }
}

@Composable
fun StatisticsCard(
    title: String,
    value: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                textAlign = TextAlign.Center
            )
            Text(
                text = value.toString(),
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun SuccessCard(statisticsState: StatisticsState, longestStreak: Int) {
    val totalPrayer = remember (statisticsState){ statisticsState.totalPrayers }
    val completedPrayer = remember(statisticsState) { statisticsState.completedPrayers }
    val totalPercentage = animateFloatAsState(
        targetValue = if (totalPrayer != 0) {
            (completedPrayer.toFloat() / totalPrayer.toFloat() * 100).coerceIn(0f, 100f)
        } else {
            0f
        },
        animationSpec = tween(2000)
    )
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .animateContentSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Başarı Durumu",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = String.format(Locale.getDefault(), "%.1f", totalPercentage.value) + "%",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.primary,
                    )
                    Text(
                        text = "Tamamlama",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
                
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "$longestStreak gün",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "En Uzun Seri",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
            
            // Motivasyon Mesajı
            Text(
                text = getMotivationMessage(totalPercentage.value.toInt()),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}
@Composable
fun DetailedPrayerStatistics(completedPercentageList : List<Float>) {
    var expandedState by remember { mutableStateOf(false) }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .animateContentSize()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expandedState = !expandedState },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.statistics_detail),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Icon(
                    imageVector = if (expandedState) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = "Detayları Göster/Gizle",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            
            if (expandedState) {
                Spacer(modifier = Modifier.height(16.dp))
                DetailedPrayerRow(stringResource(R.string.morning), completedPercentageList.getOrNull(0)?:0f)
                DetailedPrayerRow(stringResource(R.string.noon), completedPercentageList.getOrNull(1)?:0f)
                DetailedPrayerRow(stringResource(R.string.afternoon), completedPercentageList.getOrNull(2)?:0f)
                DetailedPrayerRow(stringResource(R.string.evening), completedPercentageList.getOrNull(3)?:0f)
                DetailedPrayerRow(stringResource(R.string.night), completedPercentageList.getOrNull(4)?:0f)
            }
        }
    }
}

@Composable
fun DetailedPrayerRow(prayerName: String, percentage: Float) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = prayerName,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(
                text = "%$percentage",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }

        LinearProgressIndicator(
            drawStopIndicator = {
                drawStopIndicator(drawScope = this,
                    stopSize = 0.dp,
                    color = Color.Transparent,
                    strokeCap = StrokeCap.Round)
            },
            gapSize = 0.dp,
            progress ={
                if (percentage > 100) 1f
                else percentage / 100f
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .padding(top = 4.dp),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
        )
    }
}

private fun getMotivationMessage(percentage: Int): String {
    return when {
        percentage >= 90 -> "Mükemmel! Harika bir istatistik yakaladınız! 🌟"
        percentage >= 70 -> "Çok iyi gidiyorsunuz! Böyle devam edin! 💪"
        percentage >= 50 -> "İyi gidiyorsunuz, daha da iyisini yapabilirsiniz! 🌱"
        else -> "Her yeni gün yeni bir başlangıç! 🌅"
    }
}
