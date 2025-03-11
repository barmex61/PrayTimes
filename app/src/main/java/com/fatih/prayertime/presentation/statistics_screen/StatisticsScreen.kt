package com.fatih.prayertime.presentation.statistics_screen

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.fatih.prayertime.data.local.entity.PrayerStatisticsEntity
import com.fatih.prayertime.util.composables.TitleView
import com.fatih.prayertime.util.config.ThemeConfig.colors
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStart
import com.patrykandpatrick.vico.compose.cartesian.cartesianLayerPadding
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberColumnCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.layer.stacked
import com.patrykandpatrick.vico.compose.cartesian.marker.rememberDefaultCartesianMarker
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoZoomState
import com.patrykandpatrick.vico.compose.common.component.rememberLineComponent
import com.patrykandpatrick.vico.compose.common.component.rememberTextComponent
import com.patrykandpatrick.vico.compose.common.component.shapeComponent
import com.patrykandpatrick.vico.compose.common.fill
import com.patrykandpatrick.vico.compose.common.insets
import com.patrykandpatrick.vico.compose.common.rememberHorizontalLegend
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModel
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.core.cartesian.data.ColumnCartesianLayerModel
import com.patrykandpatrick.vico.core.cartesian.data.candlestickSeries
import com.patrykandpatrick.vico.core.cartesian.data.columnSeries
import com.patrykandpatrick.vico.core.cartesian.layer.ColumnCartesianLayer
import com.patrykandpatrick.vico.core.cartesian.marker.DefaultCartesianMarker
import com.patrykandpatrick.vico.core.common.LegendItem
import com.patrykandpatrick.vico.core.common.component.TextComponent
import com.patrykandpatrick.vico.core.common.data.ExtraStore
import com.patrykandpatrick.vico.core.common.shape.CorneredShape
import ir.ehsannarmani.compose_charts.ColumnChart
import ir.ehsannarmani.compose_charts.PieChart
import ir.ehsannarmani.compose_charts.models.BarProperties
import ir.ehsannarmani.compose_charts.models.Bars
import ir.ehsannarmani.compose_charts.models.DividerProperties
import ir.ehsannarmani.compose_charts.models.GridProperties
import ir.ehsannarmani.compose_charts.models.HorizontalIndicatorProperties
import ir.ehsannarmani.compose_charts.models.IndicatorCount
import ir.ehsannarmani.compose_charts.models.LabelHelperProperties
import ir.ehsannarmani.compose_charts.models.LabelProperties
import ir.ehsannarmani.compose_charts.models.LineProperties
import ir.ehsannarmani.compose_charts.models.Pie
import ir.ehsannarmani.compose_charts.models.PopupProperties
import kotlinx.coroutines.runBlocking
import java.text.DecimalFormat
import kotlin.collections.forEachIndexed

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(
    bottomPaddingValue: Dp,
    viewModel: StatisticsViewModel = hiltViewModel()
) {
    val statistics by viewModel.statistics.collectAsState()
    val statisticsSummary by viewModel.statisticsSummary.collectAsState()
    var showDatePicker by remember { mutableStateOf(false) }
    var selectedDateRange by remember { mutableStateOf("Son 7 Gün") }
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = bottomPaddingValue)
            .verticalScroll(scrollState)
    ) {

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
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
                FilledTonalIconButton(
                    onClick = { showDatePicker = true }
                ) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = "Tarih Seç"
                    )
                }
            }
        }


        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            StatisticsCard(
                title = "Toplam Kılınan",
                value = statisticsSummary.completedPrayers.toString(),
                modifier = Modifier.weight(1f)
            )
            StatisticsCard(
                title = "Kaçırılan",
                value = statisticsSummary.missedPrayers.toString(),
                modifier = Modifier.weight(1f)
            )
        }



        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(16.dp)
        ) {
            StatisticsChart(
                statistics = statistics,
            )
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Başlangıç",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = statisticsSummary.startDate,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Bitiş",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = statisticsSummary.endDate,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Toplam Namaz",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = statisticsSummary.totalPrayers.toString(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }
    }

    // Tarih seçici dialog
    if (showDatePicker) {
        AlertDialog(
            onDismissRequest = { showDatePicker = false },
            title = {
                Text(
                    text = "Tarih Aralığı Seç",
                    style = MaterialTheme.typography.titleLarge
                )
            },
            text = {
                Column {
                    listOf(
                        "Son 7 Gün",
                        "Son 30 Gün",
                        "Son 3 Ay",
                        "Tüm Zamanlar"
                    ).forEach { range ->
                        ListItem(
                            headlineContent = { Text(range) },
                            modifier = Modifier.clickable {
                                selectedDateRange = range
                                showDatePicker = false
                                // TODO: viewModel'e seçilen aralığı gönder
                            }
                        )
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("İptal")
                }
            }
        )
    }

    TitleView("İstatistikler")
}

@Composable
fun StatisticsChart(
    statistics: List<PrayerStatisticsEntity>,
) {
    if (statistics.isEmpty()) return
    println("statistics ${statistics.size}")
    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondary
    val dailyPrayerCounts = remember(statistics) {
        statistics.groupBy { it.date }
            .mapValues { (_, prayers) -> prayers.count { it.isCompleted } }
            .toList()
            .sortedBy { it.first }
            .map { (date, count) ->
                Bars(
                    label = date.substring(0,5),
                    values = listOf(
                        Bars.Data(
                            value = count.toDouble(),
                            color = SolidColor(primaryColor)
                        ),
                        Bars.Data(
                            value = 5.0 - count.toDouble(),
                            color = SolidColor(secondaryColor)
                        )
                    )
                )
            }
    }
    val modelProducer = remember { CartesianChartModelProducer() }
    // Use `runBlocking` only for previews, which don’t support asynchronous execution.
    LaunchedEffect(Unit) {
        modelProducer.runTransaction {
            // Learn more: https://patrykandpatrick.com/eji9zq.
            columnSeries { y.values.forEach { series(x, it) } }
            extras { it[LegendLabelKey] = y.keys }
        }
    }

    println(dailyPrayerCounts.size)

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = CardDefaults.elevatedShape,
    ) {

        JetpackComposeDailyDigitalMediaUse(modelProducer)
   /*
        ColumnChart(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            data = dailyPrayerCounts ,
            indicatorProperties = HorizontalIndicatorProperties(
                textStyle = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onPrimaryContainer),
                indicators = listOf(5.0,4.0,3.0,2.0,1.0,0.0),
                count = IndicatorCount.StepBased(1.0),
                contentBuilder = { value ->
                    value.toInt().toString()
                }
            ),
            labelProperties = LabelProperties(
                textStyle = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onPrimaryContainer),
                enabled = true
            ),
            dividerProperties = DividerProperties(
                xAxisProperties = LineProperties(
                    color = SolidColor(MaterialTheme.colorScheme.onPrimaryContainer),
                    thickness = 1.dp
                ),
                yAxisProperties = LineProperties(
                    color = SolidColor(MaterialTheme.colorScheme.onPrimaryContainer),
                    thickness = 1.dp
                )

            ),
            gridProperties = GridProperties(
                xAxisProperties = GridProperties.AxisProperties(
                    color = SolidColor(MaterialTheme.colorScheme.onPrimaryContainer),
                    thickness = 0.5.dp
                ),
                yAxisProperties = GridProperties.AxisProperties(
                    color = SolidColor(MaterialTheme.colorScheme.onPrimaryContainer),
                    thickness = 0.5.dp
                )
            ),
            popupProperties = PopupProperties(
                contentBuilder = { value ->
                    value.toInt().toString()
                },
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                textStyle = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onPrimaryContainer)
            ),
            labelHelperProperties = LabelHelperProperties(
                textStyle = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onPrimaryContainer),
            ),
            barProperties = BarProperties(
                cornerRadius = Bars.Data.Radius.Rectangle(topRight = 6.dp, topLeft = 6.dp),
                spacing = 2.dp,
                thickness = 18.dp
            ),
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            ),
            maxValue = 5.0,
            minValue = 0.0
        )  */
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
                text = value,
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )
        }
    }
}
private val LegendLabelKey = ExtraStore.Key<Set<String>>()
private val YDecimalFormat = DecimalFormat("#.#")
private val StartAxisValueFormatter = CartesianValueFormatter.decimal(YDecimalFormat)
private val StartAxisItemPlacer = VerticalAxis.ItemPlacer.step({ 1.0 })
private val MarkerValueFormatter = DefaultCartesianMarker.ValueFormatter.default(YDecimalFormat)

@Composable
private fun JetpackComposeDailyDigitalMediaUse(
    modelProducer: CartesianChartModelProducer,
    modifier: Modifier = Modifier,
) {
    val columnColors = listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.onError)
    //alttaki kategorilerin rengi
    val legendItemLabelComponent = rememberTextComponent(Color.Black)
    CartesianChartHost(
        chart =
            rememberCartesianChart(
                rememberColumnCartesianLayer(
                    columnProvider =
                        ColumnCartesianLayer.ColumnProvider.series(
                            columnColors.map { color ->
                                rememberLineComponent(fill = fill(color), thickness = 16.dp)
                            }
                        ),
                    columnCollectionSpacing = 32.dp,
                    mergeMode = { ColumnCartesianLayer.MergeMode.stacked() },
                ),
                startAxis =
                    VerticalAxis.rememberStart(
                        label = TextComponent(MaterialTheme.colorScheme.onPrimaryContainer.toArgb()),
                        valueFormatter = StartAxisValueFormatter,
                        itemPlacer = StartAxisItemPlacer,
                    ),
                bottomAxis =
                    HorizontalAxis.rememberBottom(
                        label = TextComponent(color = MaterialTheme.colorScheme.onPrimaryContainer.toArgb()),
                        itemPlacer = remember { HorizontalAxis.ItemPlacer.segmented() }
                    ),
                marker = rememberDefaultCartesianMarker(label = rememberTextComponent(color = Color.Black), valueFormatter = MarkerValueFormatter),
                layerPadding = { cartesianLayerPadding(scalableStart = 16.dp, scalableEnd = 16.dp) },
                legend =
                    rememberHorizontalLegend(
                        items = { extraStore ->
                            extraStore[LegendLabelKey].forEachIndexed { index, label ->
                                add(
                                    LegendItem(
                                        shapeComponent(fill(columnColors[index]), CorneredShape.Pill),
                                        legendItemLabelComponent,
                                        label,
                                    )
                                )
                            }
                        },
                        padding = insets(top = 16.dp),
                    ),
            ),
        modelProducer = modelProducer,
        modifier = modifier.height(256.dp),
        zoomState = rememberVicoZoomState(zoomEnabled = true),
    )
}

private val x = (2008..2018).toList()

private val y =
    mapOf(
        "Kılınan" to listOf<Number>(2, 1, 4, 5, 0, 1, 3, 3, 2, 1, 4),
        "Kılınmayan" to listOf(3, 4, 1, 0, 5, 4, 2, 2, 3, 4, 1),
    )

@Composable
fun PreviewBox(content: @Composable BoxScope.() -> Unit) {
    Box(modifier = Modifier.background(Color.White).padding(16.dp), content = content)
}