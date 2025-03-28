package com.fatih.prayertime.presentation.settings_screen

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandIn
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.fatih.prayertime.R
import com.fatih.prayertime.domain.model.PrayerAlarm
import com.fatih.prayertime.domain.model.ThemeOption
import com.fatih.prayertime.util.model.enums.PrayTimesString
import com.fatih.prayertime.util.composables.TitleView


@Composable
fun SettingsScreen(modifier: Modifier, settingsScreenViewModel: SettingsScreenViewModel = hiltViewModel()) {
    val showSelectedGlobalAlarmOffsetSelectionDialog = remember { mutableStateOf(false) }
    val selectedPrayerAlarm = remember { mutableStateOf<PrayerAlarm?>(null) }
    val uiSettings by settingsScreenViewModel.settingsState.collectAsState()
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .verticalScroll(scrollState)
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primaryContainer,RoundedCornerShape(16.dp))
                .padding(24.dp)
        ) {
            Text(
                text = stringResource(R.string.settings),
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SettingsSection(
                title = stringResource(R.string.appearance),
                icon = ImageVector.vectorResource(R.drawable.palette)
            ) {
                AppearanceSettingsSection(uiSettings.selectedTheme) { settingsScreenViewModel.updateTheme(it) }
            }

            SettingsSection(
                title = stringResource(R.string.prayer_time_calculation),
                icon = ImageVector.vectorResource(R.drawable.morning)
            ) {
                PrayerTimeCalculationSection(
                    selectedMethod = uiSettings.prayerCalculationMethod?:1,
                    onMethodChange = { settingsScreenViewModel.updatePrayerCalculationMethod(it) },
                    onTuneValuesChange = { settingsScreenViewModel.updatePrayerTimeTuneValues(it) },
                    tuneValues = uiSettings.prayerTimeTuneValues,
                )
            }

            SettingsSection(
                title = stringResource(R.string.notification),
                icon = Icons.Default.Notifications
            ) {
                SettingsSwitchItem(
                    title = stringResource(R.string.vibration),
                    subtitle = stringResource(R.string.vibration_description),
                    isChecked = uiSettings.vibrationEnabled,
                    onCheckedChange = { settingsScreenViewModel.toggleVibration() }
                )
            }

            SettingsSection(
                title = stringResource(R.string.alarms),
                icon = ImageVector.vectorResource(R.drawable.alarm_icon)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    uiSettings.prayerAlarms.forEach { alarm ->
                        AlarmSettingItem(
                            alarm = alarm,
                            onToggle = settingsScreenViewModel::togglePrayerNotification,
                            onMinuteToggle = { selectedAlarm ->
                                showSelectedGlobalAlarmOffsetSelectionDialog.value = true
                                selectedPrayerAlarm.value = selectedAlarm
                            }
                        )
                    }
                }
            }

            SettingsSection(
                title = stringResource(R.string.others),
                icon = Icons.Default.MoreVert
            ) {
                SettingsSwitchItem(
                    title = stringResource(R.string.mute_friday),
                    subtitle = stringResource(R.string.mute_friday_description),
                    isChecked = uiSettings.silenceWhenCuma,
                    onCheckedChange = { settingsScreenViewModel.toggleCuma() }
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
        color = MaterialTheme.colorScheme.primaryContainer,
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
        color = MaterialTheme.colorScheme.secondaryContainer
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
        color = MaterialTheme.colorScheme.secondaryContainer
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
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp)),
                color = MaterialTheme.colorScheme.secondaryContainer
            ){
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onThemeSelected(theme) }
                        .padding(vertical = 8.dp, horizontal = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(PrayTimesString.fromString(theme.name).stringResId),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    RadioButton(
                        selected = selectedTheme == theme,
                        onClick = { onThemeSelected(theme) }
                    )
                }
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

@Composable
fun PrayerTimeCalculationSection(
    selectedMethod: Int,
    onMethodChange: (Int) -> Unit,
    tuneValues: Map<String, Int>,
    onTuneValuesChange: (Map<String, Int>) -> Unit

) {
    var showMethodDialog by remember { mutableStateOf(false) }
    var showTuneDialog by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp)),
        color = MaterialTheme.colorScheme.secondaryContainer
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showMethodDialog = !showMethodDialog }
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = stringResource(R.string.calculation_method),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = getCalculationMethodName(selectedMethod),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                val methodDialogArrow =
                    if (showMethodDialog) ImageVector.vectorResource(R.drawable.arrow_down) else ImageVector.vectorResource(
                        R.drawable.arrow_right
                    )
                AnimatedContent(
                    targetState = methodDialogArrow
                ) {
                    Icon(
                        modifier = Modifier.animateContentSize(),
                        imageVector = it,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            AnimatedVisibility(
                visible = showMethodDialog,
                enter = fadeIn(tween(400)) + slideInVertically(),
                exit = fadeOut(tween(400)) + slideOutVertically()
            ) {
                PrayerCalculationMethodDialog(
                    selectedMethod = selectedMethod,
                    onMethodSelect = {
                        onMethodChange(it)
                        showMethodDialog = false
                    }
                )
            }


            HorizontalDivider()

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showTuneDialog = !showTuneDialog }
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = stringResource(R.string.prayer_time_adjustments),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = stringResource(R.string.prayer_time_adjustments_description),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                val tuneDialogArrow = if (showTuneDialog)  ImageVector.vectorResource(R.drawable.arrow_down) else ImageVector.vectorResource(R.drawable.arrow_right)
                AnimatedContent(
                    targetState = tuneDialogArrow
                ) {
                    Icon(
                        modifier = Modifier.animateContentSize(),
                        imageVector = it,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            AnimatedVisibility(
                visible = showTuneDialog,
                enter = fadeIn(tween(400)) + slideInVertically(),
                exit = fadeOut(tween(400)) + slideOutVertically()
            ) {
                PrayerTimeTuneDialog(
                    currentTuneValues = tuneValues,
                    onTuneValuesChange = {
                        onTuneValuesChange(it)
                        showTuneDialog = false
                    },
                    onDismiss = { showTuneDialog = false }
                )
            }

        }
    }
}

@Composable
fun PrayerCalculationMethodDialog(
    selectedMethod: Int,
    onMethodSelect: (Int) -> Unit,
) {
    val methods = listOf(
        Pair(0, "Jafari / Shia Ithna-Ashari"),
        Pair(1, "University of Islamic Sciences, Karachi"),
        Pair(2, "Islamic Society of North America (ISNA)"),
        Pair(3, "Muslim World League (MWL)"),
        Pair(4, "Umm Al-Qura University, Makkah"),
        Pair(5, "Egyptian General Authority of Survey"),
        Pair(7, "Institute of Geophysics, University of Tehran"),
        Pair(8, "Gulf Region"),
        Pair(9, "Kuwait"),
        Pair(10, "Qatar"),
        Pair(11, "Majlis Ugama Islam Singapura, Singapore"),
        Pair(12, "Union Organization islamic de France"),
        Pair(13, "Diyanet İşleri Başkanlığı, Turkey"),
        Pair(14, "Spiritual Administration of Muslims of Russia"),
        Pair(15, "Moonsighting Committee Worldwide"),
        Pair(16, "Dubai (experimental)"),
        Pair(17, "Jabatan Kemajuan Islam Malaysia (JAKIM)"),
        Pair(18, "Tunisia"),
        Pair(19, "Algeria"),
        Pair(20, "KEMENAG - Kementerian Agama Republik Indonesia"),
        Pair(21, "Morocco"),
        Pair(22, "Comunidade Islamica de Lisboa"),
        Pair(23, "Ministry of Awqaf, Islamic Affairs and Holy Places, Jordan")
    )

    LazyColumn(
        modifier = Modifier
            .heightIn(min = 200.dp, max = 400.dp)
    ) {
        items(methods) { (methodId, methodName) ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onMethodSelect(methodId) }
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = selectedMethod == methodId,
                    onClick = { onMethodSelect(methodId) }
                )
                Text(
                    text = methodName,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    }
}

@Composable
fun PrayerTimeTuneDialog(
    currentTuneValues: Map<String, Int>,
    onTuneValuesChange: (Map<String, Int>) -> Unit,
    onDismiss: () -> Unit
) {
    val tuneMap = remember {
        mutableStateOf(currentTuneValues.toMap())
    }
    
    val prayerTimeKeys = listOf(
            Pair("fajr", stringResource(R.string.morning)),
            Pair("dhuhr", stringResource(R.string.noon) ),
            Pair("asr", stringResource(R.string.afternoon)),
            Pair("maghrib", stringResource(R.string.evening)),
            Pair("isha", stringResource(R.string.night))
        )

    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f))
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Text(
                text = stringResource(R.string.prayer_time_adjustments),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            Text(
                text = stringResource(R.string.prayer_time_adjustments_tune_description),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            LazyColumn(
                modifier = Modifier
                    .heightIn(min = 100.dp, max = 300.dp)
            ) {
                items(prayerTimeKeys) { (key, name) ->
                    PrayerTimeTuneItem(
                        prayerName = name,
                        value = tuneMap.value.getOrDefault(key, 0),
                        onValueChange = { 
                            val updatedMap = tuneMap.value.toMutableMap()
                            updatedMap[key] = it
                            tuneMap.value = updatedMap
                        }
                    )
                }
            }
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End)
            ) {
                Button(
                    onClick = { onDismiss() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                ) {
                    Text(text = stringResource(R.string.close))
                }
                
                Button(
                    onClick = { 
                        val finalMap = currentTuneValues.toMutableMap()
                        tuneMap.value.forEach { (key, value) ->
                            finalMap[key] = value
                        }
                        onTuneValuesChange(finalMap) 
                    }
                ) {
                    Text(text = stringResource(R.string.apply))
                }
            }
        }
    }
}

@Composable
fun PrayerTimeTuneItem(
    prayerName: String,
    value: Int,
    onValueChange: (Int) -> Unit
) {
    var textValue by remember { mutableStateOf(value.toString()) }
    val focusManager = LocalFocusManager.current
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = prayerName,
            style = MaterialTheme.typography.bodyLarge
        )
        
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .clickable {
                        val newValue = (textValue.toIntOrNull() ?: 0) - 1
                        textValue = newValue.toString()
                        onValueChange(newValue)
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "-",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            
            BasicTextField(
                value = textValue,
                onValueChange = { 
                    if (it.isEmpty() || it == "-" || it.toIntOrNull() != null) {
                        textValue = it
                        it.toIntOrNull()?.let { intValue -> onValueChange(intValue) }
                    }
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = { focusManager.clearFocus() }
                ),
                modifier = Modifier
                    .width(50.dp)
                    .height(32.dp)
                    .background(
                        MaterialTheme.colorScheme.surfaceVariant,
                        RoundedCornerShape(4.dp)
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                textStyle = MaterialTheme.typography.bodyMedium.copy(
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
            
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .clickable {
                        val newValue = (textValue.toIntOrNull() ?: 0) + 1
                        textValue = newValue.toString()
                        onValueChange(newValue)
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "+",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            
            Text(
                text = "dk",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = 4.dp)
            )
        }
    }
}

fun getCalculationMethodName(method: Int): String {
    return when (method) {
        0 -> "Jafari / Shia Ithna-Ashari"
        1 -> "University of Islamic Sciences, Karachi"
        2 -> "Islamic Society of North America (ISNA)"
        3 -> "Muslim World League (MWL)"
        4 -> "Umm Al-Qura University, Makkah"
        5 -> "Egyptian General Authority of Survey"
        7 -> "Institute of Geophysics, University of Tehran"
        8 -> "Gulf Region"
        9 -> "Kuwait"
        10 -> "Qatar"
        11 -> "Majlis Ugama Islam Singapura, Singapore"
        12 -> "Union Organization islamic de France"
        13 -> "Diyanet İşleri Başkanlığı, Turkey"
        14 -> "Spiritual Administration of Muslims of Russia"
        15 -> "Moonsighting Committee Worldwide"
        16 -> "Dubai (experimental)"
        17 -> "Jabatan Kemajuan Islam Malaysia (JAKIM)"
        18 -> "Tunisia"
        19 -> "Algeria"
        20 -> "KEMENAG - Kementerian Agama Republik Indonesia"
        21 -> "Morocco"
        22 -> "Comunidade Islamica de Lisboa"
        23 -> "Ministry of Awqaf, Islamic Affairs and Holy Places, Jordan"
        else -> "Islamic Society of North America (ISNA)"
    }
}