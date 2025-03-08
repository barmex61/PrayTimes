package com.fatih.prayertime.presentation.calendar_screen

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandIn
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.animation.with
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.fatih.prayertime.domain.model.IslamicDaysData
import com.fatih.prayertime.util.ErrorView
import com.fatih.prayertime.util.LoadingView
import com.fatih.prayertime.util.Status
import com.fatih.prayertime.util.TitleView
import org.threeten.bp.LocalDate
import kotlin.random.Random

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun CalendarScreen(bottomPaddingValues : Dp,calendarScreenViewModel: CalendarScreenViewModel = hiltViewModel()) {
    val selectedLocalDate by calendarScreenViewModel.searchLocalDate.collectAsState()
    val monthlyIslamicCalendar by calendarScreenViewModel.monthlyIslamicCalendar.collectAsState()
    val haptics = LocalHapticFeedback.current
    when(monthlyIslamicCalendar.status){
        Status.SUCCESS , Status.LOADING-> {
            Column(modifier = Modifier.padding(bottom = bottomPaddingValues)) {
                MonthNavigation(selectedLocalDate,calendarScreenViewModel,haptics)
                AnimatedContent(
                    targetState = monthlyIslamicCalendar.data,
                    transitionSpec = {
                        expandIn(tween(750), expandFrom = Alignment.TopCenter) + fadeIn() togetherWith
                                shrinkOut(tween(750), shrinkTowards = Alignment.BottomEnd)  + fadeOut()
                    }
                ) { islamicDays ->
                    islamicDays?.let {
                        IslamicCalendar(islamicDays = it,haptics,calendarScreenViewModel)
                    }
                }
            }
            if (monthlyIslamicCalendar.status == Status.LOADING){
                LoadingView()
            }
        }
        Status.ERROR -> {
            ErrorView(message = monthlyIslamicCalendar.message ?: "An unexpected error occurred"){
                calendarScreenViewModel.getMonthlyIslamicCalendar()
            }
        }
    }
    TitleView("Islamic Calendar")
}

@Composable
fun MonthNavigation(selectedLocalDate: LocalDate, calendarScreenViewModel: CalendarScreenViewModel, haptic : HapticFeedback) {
    val currentDateString = calendarScreenViewModel.formatLocalDateToString(selectedLocalDate)
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(onClick = {
            calendarScreenViewModel.updateSearchMonthAndYear(selectedLocalDate.minusMonths(1))
            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
        }) {
            Text("<")
        }
        Text(text = currentDateString, style = MaterialTheme.typography.titleMedium)
        Button(onClick = {
            calendarScreenViewModel.updateSearchMonthAndYear(selectedLocalDate.plusMonths(1))
            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
        }) {
            Text(">")
        }
    }
}

@Composable
fun IslamicCalendar(islamicDays: List<IslamicDaysData>, haptic: HapticFeedback,calendarScreenViewModel: CalendarScreenViewModel) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(90.dp),
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 5.dp)
    ) {
        items(islamicDays) { day ->
            DayCard(day = day,haptic = haptic, calendarScreenViewModel)
        }
    }
}
@Composable
fun DayCard(day: IslamicDaysData, haptic : HapticFeedback,calendarScreenViewModel: CalendarScreenViewModel) {
    var isVisible by remember { mutableStateOf(false) }
    val isToday = remember {  calendarScreenViewModel.isToday(day)}
    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(tween(Random.nextInt(2000))) + slideInVertically(tween(Random.nextInt(2000)){ height -> height}),
        exit = fadeOut(tween(Random.nextInt(2000))) + slideOutVertically(tween(Random.nextInt(2000)){ height -> -height})
    ) {
        Card(
            elevation = CardDefaults.cardElevation(5.dp),
            modifier = Modifier
                .width(IntrinsicSize.Max)
                .heightIn(75.dp, 200.dp)
                .animateContentSize()
                .padding(bottom = 10.dp, start = 5.dp, end = 5.dp, top = 4.dp)
            ,
            border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.primary),
            colors = CardDefaults.cardColors(if(isToday) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primaryContainer),
            onClick = {
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            }
        ) {
            Column(
                modifier = Modifier.padding(start = 5.dp, top = 5.dp, bottom = 5.dp)
            ) {
                Text(
                    text = day.date,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1
                )
                Text(
                    text = day.islamicMonth,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isToday) Color.White else MaterialTheme.colorScheme.primary
                )
                day.islamicDay.forEach { holiday ->
                    Text(
                        text = holiday,
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isToday) Color.White else MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
    LaunchedEffect(Unit) {
        isVisible = true
    }
}
