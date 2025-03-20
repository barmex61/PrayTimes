package com.fatih.prayertime.presentation.settings_screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandIn
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications

import androidx.compose.material.icons.filled.MoreVert


import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.fatih.prayertime.R
import com.fatih.prayertime.domain.model.PrayerAlarm
import com.fatih.prayertime.domain.model.ThemeOption
import com.fatih.prayertime.presentation.main_activity.AppViewModel
import com.fatih.prayertime.util.model.enums.PrayTimesString
import com.fatih.prayertime.util.composables.TitleView


@Composable
fun SettingsScreen(modifier: Modifier, appViewModel: AppViewModel = hiltViewModel()) {
    val showSelectedGlobalAlarmOffsetSelectionDialog = remember { mutableStateOf(false) }
    val selectedPrayerAlarm = remember { mutableStateOf<PrayerAlarm?>(null) }
    val uiSettings by appViewModel.settingsState.collectAsState()
    val scrollState = rememberScrollState()

    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            modifier = Modifier
                .verticalScroll(scrollState)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primary)
                    .padding(24.dp)
            ) {
                Text(
                    text = stringResource(R.string.settings),
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }

            // Settings Content
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Görünüm Ayarları
                SettingsSection(
                    title = stringResource(R.string.appearance),
                    icon = ImageVector.vectorResource(R.drawable.palette)
                ) {
                    AppearanceSettingsSection(uiSettings.selectedTheme) { appViewModel.updateTheme(it) }
                }

                // Bildirim Ayarları
                SettingsSection(
                    title = stringResource(R.string.notification),
                    icon = Icons.Default.Notifications
                ) {
                    SettingsSwitchItem(
                        title = stringResource(R.string.vibration),
                        subtitle = stringResource(R.string.vibration_description),
                        isChecked = uiSettings.vibrationEnabled,
                        onCheckedChange = { appViewModel.toggleVibration() }
                    )
                }

                // Alarm Ayarları
                SettingsSection(
                    title = stringResource(R.string.alarms),
                    icon = ImageVector.vectorResource(R.drawable.alarm_icon)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        prayerAlarms.forEach { alarm ->
                            AlarmSettingItem(
                                alarm = alarm,
                                onToggle = appViewModel::togglePrayerNotification,
                                onMinuteToggle = { selectedAlarm ->
                                    showSelectedGlobalAlarmOffsetSelectionDialog.value = true
                                    selectedPrayerAlarm.value = selectedAlarm
                                }
                            )
                        }
                    }
                }

                // Diğer Ayarlar
                SettingsSection(
                    title = stringResource(R.string.others),
                    icon = Icons.Default.MoreVert
                ) {
                    SettingsSwitchItem(
                        title = stringResource(R.string.mute_friday),
                        subtitle = stringResource(R.string.mute_friday_description),
                        isChecked = uiSettings.silenceWhenCuma,
                        onCheckedChange = { appViewModel.toggleCuma() }
                    )
                }
            }
        }

        AnimatedVisibility(
            visible = showSelectedGlobalAlarmOffsetSelectionDialog.value,
            enter = fadeIn(tween(700)) + expandIn(tween(700), expandFrom = Alignment.Center),
            exit = fadeOut(tween(700)) + shrinkOut(tween(700), shrinkTowards = Alignment.Center)
        ) {
            selectedPrayerAlarm.value?.let {
                OffsetMinuteSelectionHeader(it) { showSelectedGlobalAlarmOffsetSelectionDialog.value = false }
            }
        }
    }
    TitleView("Settings")
}

@Composable
private fun SettingsSection(
    title: String,
    icon: ImageVector,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp)),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.padding(bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            content()
        }
    }
}

@Composable
private fun SettingsSwitchItem(
    title: String,
    subtitle: String? = null,
    isChecked: Boolean,
    onCheckedChange: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onCheckedChange() },
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Switch(
                checked = isChecked,
                onCheckedChange = { onCheckedChange() }
            )
        }
    }
}

@Composable
private fun AlarmSettingItem(
    alarm: PrayerAlarm,
    onToggle: (PrayerAlarm) -> Unit,
    onMinuteToggle: (PrayerAlarm) -> Unit
) {
    val alphaValue = if (alarm.isEnabled) 1f else 0.5f
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp)),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(PrayTimesString.fromString(alarm.alarmType).stringResId),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = alphaValue)
                )
                Text(
                    text = stringResource(R.string.alarm_offset_format, alarm.alarmOffset),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = alphaValue),
                    modifier = Modifier.clickable { onMinuteToggle(alarm) }
                )
            }
            Switch(
                checked = alarm.isEnabled,
                onCheckedChange = { onToggle(alarm.copy(isEnabled = !alarm.isEnabled)) }
            )
        }
    }
}

@Composable
fun AppearanceSettingsSection(selectedTheme: ThemeOption, onThemeSelected: (ThemeOption) -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ThemeOption.entries.forEach { theme ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onThemeSelected(theme) }
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(PrayTimesString.fromString(theme.name).stringResId),
                    style = MaterialTheme.typography.bodyLarge
                )
                RadioButton(
                    selected = selectedTheme == theme,
                    onClick = { onThemeSelected(theme) }
                )
            }
        }
    }
}

@Composable
fun OffsetMinuteSelectionHeader(selectedPrayerAlarm: PrayerAlarm, closeDialog : () -> Unit) {

    Box(modifier = Modifier
        .fillMaxSize(1f)
        .background(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.07f)
        )
        .clickable(enabled = false) {}, contentAlignment = Alignment.Center){
        Box(modifier = Modifier
            .wrapContentSize()
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.9f))
            .padding(12.dp), contentAlignment = Alignment.Center){
            Column (horizontalAlignment = Alignment.CenterHorizontally){
                OffsetMinuteSelectionCompose(selectedPrayerAlarm, closeDialog )
            }
        }
    }
}

@Composable
fun OffsetMinuteSelectionCompose(selectedPrayerAlarm: PrayerAlarm, closeDialog: () -> Unit) {
    val settingsViewModel : SettingsScreenViewModel = hiltViewModel()
    val focusManager = LocalFocusManager.current
    var newAlarmOffset by remember { mutableLongStateOf(selectedPrayerAlarm.alarmOffset) }
    Row (horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
        Button(
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.9f)),
            onClick = {
                newAlarmOffset = (newAlarmOffset-1).coerceAtLeast(0)
            },
        ) {
            Text(textAlign = TextAlign.Center, text = "-", style = MaterialTheme.typography.headlineMedium, color = androidx.compose.ui.graphics.Color.Black)
        }
        Spacer(modifier = Modifier.size(20.dp))
        BasicTextField(
            modifier = Modifier.width(IntrinsicSize.Min),
            value = newAlarmOffset.toString(),
            onValueChange = { newString ->
                if (newString.all { it.isDigit() } && newString.length <= 3 ) {
                    newAlarmOffset = newString.toLongOrNull() ?: 0L
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = {
                focusManager.clearFocus()
            }),
            textStyle = MaterialTheme.typography.headlineLarge)
        Spacer(modifier = Modifier.size(20.dp))
        Button(
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.9f)),
            onClick = {
                newAlarmOffset += 1
            },
        ) {
            Text(textAlign = TextAlign.Center, text = "+", style = MaterialTheme.typography.headlineMedium, color = androidx.compose.ui.graphics.Color.Black)
        }
    }
    Spacer(modifier = Modifier.size(10.dp))
    Row (horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
        Button(
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.9f)),
            onClick = {
                closeDialog()
            }
        ) {
            Text(
                text = "Cancel",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primaryContainer
            )
        }
        Spacer(modifier = Modifier.size(10.dp))
        Button(
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.9f)),
            onClick = {
                settingsViewModel.updateGlobalAlarm(selectedPrayerAlarm.copy(alarmOffset = newAlarmOffset),closeDialog)
            }) {
            Text(
                text = "Save",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primaryContainer
            )
        }
    }

}