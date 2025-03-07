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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.fatih.prayertime.R
import com.fatih.prayertime.domain.model.GlobalAlarm
import com.fatih.prayertime.domain.model.ThemeOption
import com.fatih.prayertime.presentation.main_activity.AppViewModel
import com.fatih.prayertime.util.PrayTimesString
import com.fatih.prayertime.util.TitleView

@Composable
fun SettingsScreen(bottomPaddingValue: Dp,appViewModel: AppViewModel = hiltViewModel()) {
    val showSelectedGlobalAlarmOffsetSelectionDialog = remember { mutableStateOf(false) }
    val selectedGlobalAlarm = remember { mutableStateOf<GlobalAlarm?>(null) }
    val uiSettings by appViewModel.settingsState.collectAsState()
    val scrollState = rememberScrollState()

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(modifier = Modifier.verticalScroll(scrollState)) {
            SettingsHeader()
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            AppearanceSettingsCard(uiSettings.selectedTheme) { appViewModel.updateTheme(it) }
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            NotificationSettingsCard(uiSettings.vibrationEnabled) { appViewModel.toggleVibration() }
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            AlarmSettingsCard(uiSettings.prayerAlarms, appViewModel::togglePrayerNotification) { selectedAlarm ->
                showSelectedGlobalAlarmOffsetSelectionDialog.value = true
                selectedGlobalAlarm.value = selectedAlarm
            }
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            OtherSettingsCard(uiSettings.silenceWhenCuma) { appViewModel.toggleCuma() }
            Spacer(modifier = Modifier.size(25.dp + bottomPaddingValue))
        }
        AnimatedVisibility(
            visible = showSelectedGlobalAlarmOffsetSelectionDialog.value,
            enter = fadeIn(tween(700)) + expandIn(tween(700),expandFrom = Alignment.Center),
            exit = fadeOut(tween(700)) + shrinkOut(tween(700),shrinkTowards = Alignment.Center),
            ) {
            selectedGlobalAlarm.value?.let {
                OffsetMinuteSelectionHeader (it){ showSelectedGlobalAlarmOffsetSelectionDialog.value = false }
            }
        }
    }
    TitleView("Settings")
}

@Composable
fun SettingsHeader() {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        elevation = CardDefaults.cardElevation(10.dp),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 7.dp)
    ) {
        Text(
            modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp),
            text = stringResource(R.string.settings),
            style = MaterialTheme.typography.headlineSmall
        )
    }
}

@Composable
fun AppearanceSettingsCard(selectedTheme: ThemeOption, onThemeSelected: (ThemeOption) -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        elevation = CardDefaults.cardElevation(10.dp),
        shape = RoundedCornerShape(10.dp),
    ) {
        Column {
            Text(
                modifier = Modifier.padding(vertical = 3.dp, horizontal = 10.dp),
                text = stringResource(R.string.appearance),
                style = MaterialTheme.typography.titleMedium
            )
            AppearanceSettingsSection(selectedTheme, onThemeSelected)
        }
    }
}

@Composable
fun NotificationSettingsCard(vibrationEnabled: Boolean, onToggleVibration: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        elevation = CardDefaults.cardElevation(10.dp),
        shape = RoundedCornerShape(10.dp),
    ) {
        Column {
            Text(
                modifier = Modifier.padding(vertical = 7.dp, horizontal = 10.dp),
                text = stringResource(R.string.notification),
                style = MaterialTheme.typography.titleMedium
            )
            SwitchSettingItem(stringResource(R.string.vibration), vibrationEnabled, onToggleVibration)
        }
    }
}

@Composable
fun AlarmSettingsCard(
    prayerAlarms: List<GlobalAlarm>,
    onToggle: (GlobalAlarm) -> Unit,
    onMinuteToggle: (GlobalAlarm) -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        elevation = CardDefaults.cardElevation(10.dp),
        shape = RoundedCornerShape(10.dp),
    ) {
        Column {
            Text(
                modifier = Modifier.padding(vertical = 7.dp, horizontal = 10.dp),
                text = stringResource(R.string.alarms),
                style = MaterialTheme.typography.titleMedium
            )
            PrayerNotificationSettings(prayerAlarms, onToggle, onMinuteToggle)
        }
    }
}

@Composable
fun OtherSettingsCard(silenceWhenCuma: Boolean, onToggleCuma: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        elevation = CardDefaults.cardElevation(10.dp),
        shape = RoundedCornerShape(10.dp),
    ) {
        Column {
            Text(
                modifier = Modifier.padding(vertical = 7.dp, horizontal = 10.dp),
                text = stringResource(R.string.others),
                style = MaterialTheme.typography.titleMedium
            )
            SwitchSettingItem(stringResource(R.string.mute_friday), silenceWhenCuma, onToggleCuma)
        }
    }
}

@Composable
fun AppearanceSettingsSection(selectedTheme: ThemeOption, onThemeSelected: (ThemeOption) -> Unit) {
    Column {
        ThemeOption.entries.forEach { theme ->
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start,verticalAlignment = Alignment.CenterVertically) {
                RadioButton(selected = selectedTheme == theme, onClick = { onThemeSelected(theme) })
                Text(text = stringResource(PrayTimesString.fromString(theme.name).stringResId), modifier = Modifier.padding(start = 8.dp))
            }
        }
    }
}

@Composable
fun SwitchSettingItem(label: String, isChecked: Boolean, onCheckedChange: () -> Unit) {
    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 8.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text(text = label)
        Switch(checked = isChecked, onCheckedChange = { onCheckedChange() })
    }
}

@Composable
fun PrayerNotificationSettings(prayerAlarms: List<GlobalAlarm>, onToggle: (GlobalAlarm) -> Unit,onMinuteToggle : (GlobalAlarm) -> Unit) {
    Column(modifier = Modifier.padding(8.dp)) {
        prayerAlarms.forEach { alarm ->
            val alphaValue = if(alarm.isEnabled) 1f else 0.5f
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = stringResource(PrayTimesString.fromString(alarm.alarmType).stringResId),
                    modifier = Modifier.weight(2f),
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = alphaValue))
                Text(
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = alphaValue),
                    textAlign = TextAlign.Center,
                    text = "${alarm.alarmOffset} " + stringResource(R.string.min_ago),
                    style = MaterialTheme.typography.bodySmall,
                    textDecoration = TextDecoration.Underline,
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            onMinuteToggle(alarm)
                        }
                )
                Switch( checked = alarm.isEnabled,
                    onCheckedChange = { onToggle(
                        alarm.copy(isEnabled = !alarm.isEnabled)
                    )},
                    modifier = Modifier.weight(1f))
                Text(color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = alphaValue), textAlign = TextAlign.Center, text = stringResource(R.string.change_sound), modifier = Modifier.weight(1f), textDecoration = TextDecoration.Underline,style = MaterialTheme.typography.bodySmall)
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun OffsetMinuteSelectionHeader(selectedGlobalAlarm: GlobalAlarm, closeDialog : () -> Unit) {

    println("recompose")
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
                OffsetMinuteSelectionCompose(selectedGlobalAlarm, closeDialog )
            }
        }
    }
}

@Composable
fun OffsetMinuteSelectionCompose(selectedGlobalAlarm: GlobalAlarm,closeDialog: () -> Unit) {
    val settingsViewModel : SettingsScreenViewModel = hiltViewModel()
    val focusManager = LocalFocusManager.current
    var newAlarmOffset by remember { mutableLongStateOf(selectedGlobalAlarm.alarmOffset) }
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
                settingsViewModel.updateGlobalAlarm(selectedGlobalAlarm.copy(alarmOffset = newAlarmOffset),closeDialog)
            }) {
            Text(
                text = "Save",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primaryContainer
            )
        }
    }

}