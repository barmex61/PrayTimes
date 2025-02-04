package com.fatih.prayertime.presentation.settings_screen.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.fatih.prayertime.domain.model.PrayerNotification
import com.fatih.prayertime.domain.model.ThemeOption
import com.fatih.prayertime.presentation.main_activity.viewmodel.AppViewModel

@Composable
fun SettingsScreen(bottomPaddingValue : Dp) {
    val appViewModel : AppViewModel = hiltViewModel()
    val uiSettings by appViewModel.settingsState.collectAsState()
    val scrollState = rememberScrollState()
    Column(modifier = Modifier.verticalScroll(scrollState).padding(4.dp)) {
        Card (
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
            elevation = CardDefaults.cardElevation(10.dp),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth(1f).padding(top = 7.dp)
        ){
            Text(modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),text = "Ayarlar", style = MaterialTheme.typography.headlineSmall)
        }
        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

        Card (
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
            elevation = CardDefaults.cardElevation(10.dp),
            shape = RoundedCornerShape(10.dp),
        ){
            // Görünüm Seçenekleri
            Text(modifier = Modifier.padding(vertical = 3.dp, horizontal = 10.dp), text = "Görünüm", style = MaterialTheme.typography.titleMedium)
            AppearanceSettingsSection(uiSettings.selectedTheme) { appViewModel.updateTheme(it) }
        }
        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
        Card (
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
            elevation = CardDefaults.cardElevation(10.dp),
            shape = RoundedCornerShape(10.dp),
        ){
            // Bildirim Seçenekleri
            Text(modifier = Modifier.padding(vertical = 7.dp, horizontal = 10.dp),text = "Bildirim", style = MaterialTheme.typography.titleMedium)
            SwitchSettingItem("Titreşim", uiSettings.vibrationEnabled) { appViewModel.toggleVibration() }
        }
        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

        Card (
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
            elevation = CardDefaults.cardElevation(10.dp),
            shape = RoundedCornerShape(10.dp),
        ){
            Text(modifier = Modifier.padding(vertical = 7.dp, horizontal = 10.dp),text = "Alarm", style = MaterialTheme.typography.titleMedium)
            PrayerNotificationSettings(uiSettings.prayerNotifications, appViewModel::togglePrayerNotification)
        }
        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
        Card (
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
            elevation = CardDefaults.cardElevation(10.dp),
            shape = RoundedCornerShape(10.dp),
        ){
            Text(modifier = Modifier.padding(vertical = 7.dp, horizontal = 10.dp),text = "Diğer", style = MaterialTheme.typography.titleMedium)
            SwitchSettingItem("Cuma vaktinde sessize al",uiSettings.silenceWhenCuma) { appViewModel.toggleCuma() }

        }
        Spacer(modifier = Modifier.size(25.dp + bottomPaddingValue))
    }
}

@Composable
fun AppearanceSettingsSection(selectedTheme: ThemeOption, onThemeSelected: (ThemeOption) -> Unit) {
    Column {
        ThemeOption.entries.forEach { theme ->
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start,verticalAlignment = Alignment.CenterVertically) {
                RadioButton(selected = selectedTheme == theme, onClick = { onThemeSelected(theme) })
                Text(text = theme.name, modifier = Modifier.padding(start = 8.dp))
            }
        }
    }
}

@Composable
fun SwitchSettingItem(label: String, isChecked: Boolean, onCheckedChange: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text(text = label)
        Switch(checked = isChecked, onCheckedChange = { onCheckedChange() })
    }
}

@Composable
fun PrayerNotificationSettings(prayers: List<PrayerNotification>, onToggle: (String) -> Unit) {
    Column(modifier = Modifier.padding(8.dp)) {
        prayers.forEach { prayer ->
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(text = prayer.name, modifier = Modifier.weight(2f))
                Text(textAlign = TextAlign.Center,text = "${prayer.offset} dk. önce", style = MaterialTheme.typography.bodySmall, textDecoration = TextDecoration.Underline, modifier = Modifier.weight(1f))
                Switch( checked = prayer.enabled, onCheckedChange = { onToggle(prayer.name) }, modifier = Modifier.weight(1f))
                Text(textAlign = TextAlign.Center, text ="Sesi Değiştir", modifier = Modifier.weight(1f), textDecoration = TextDecoration.Underline,style = MaterialTheme.typography.bodySmall)
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}