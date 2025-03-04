package com.fatih.prayertime.util

import android.app.Application
import android.content.Context
import android.content.res.AssetManager
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavController
import com.fatih.prayertime.data.remote.dto.hadithdto.SectionDetails
import com.fatih.prayertime.data.remote.dto.hadithdto.Sections
import com.fatih.prayertime.data.remote.dto.hadithdto.HadithSectionDetails
import com.fatih.prayertime.domain.model.EsmaulHusna
import com.fatih.prayertime.domain.model.PrayTimes
import com.fatih.prayertime.domain.model.HadithSectionCardData
import com.fatih.prayertime.domain.use_case.formatted_use_cases.FormattedUseCase
import org.json.JSONArray
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import kotlin.reflect.KProperty1


fun navigateToScreen(navController: NavController, screenData: ScreenData,collectionPath : String? = null) {
    val route = if (collectionPath != null) {
        val encodedUrl = URLEncoder.encode(collectionPath, StandardCharsets.UTF_8.toString())
        screenData.route.replace("{collectionPath}", encodedUrl)
    } else {
        screenData.route
    }
    navController.navigate(route) {
        anim {
            enter = android.R.anim.slide_in_left
            exit = android.R.anim.slide_out_right
        }
        popUpTo(navController.graph.startDestinationId) { saveState = false }
        launchSingleTop = true
        restoreState = false
        if (route == "qibla") navController.popBackStack()
    }
}

fun getJsonFromAssets(fileName: String,application: Application): String {
    val assetManager: AssetManager = application.assets
    val inputStream = assetManager.open(fileName)
    return inputStream.bufferedReader().use { it.readText() }
}

fun convertJsonToEsmaulHusnaList(jsonString: String): List<EsmaulHusna> {
    val jsonArray = JSONArray(jsonString)
    val esmaulHusnaList = mutableListOf<EsmaulHusna>()

    for (i in 0 until jsonArray.length()) {
        val jsonObject = jsonArray.getJSONObject(i)
        val esmaulHusna = EsmaulHusna(
            arabicName = jsonObject.getString("arabicName"),
            name = jsonObject.getString("name"),
            shortDescription = jsonObject.getString("shortDescription"),
            longDescription = jsonObject.getString("longDescription")
        )
        esmaulHusnaList.add(esmaulHusna)
    }

    return esmaulHusnaList
}

fun getAlarmTimeForPrayTimes(dailyPrayTimes : PrayTimes,alarmType : String,alarmOffset : Long,formattedUseCase: FormattedUseCase) : String {
    val alarmTimeWithoutOffset = when(alarmType){
        PrayTimesString.Morning.name -> dailyPrayTimes.morning
        PrayTimesString.Noon.name -> dailyPrayTimes.noon
        PrayTimesString.Afternoon.name -> dailyPrayTimes.afternoon
        PrayTimesString.Evening.name -> dailyPrayTimes.evening
        PrayTimesString.Night.name -> dailyPrayTimes.night
        else -> "00:00"
    }
    return formattedUseCase.minusMinutesFromTime(alarmTimeWithoutOffset,alarmOffset)
}

fun combineSectionsAndDetails(sections: Sections, sectionDetails: SectionDetails): List<HadithSectionCardData> {
    val sectionList = sections.toList()
    val detailsList = sectionDetails.toList()

    return sectionList.zip(detailsList) { section, details ->
        HadithSectionCardData(section, details)
    }
}

fun Sections.toList(): List<String?> {
    return this::class.members
        .filterIsInstance<kotlin.reflect.KProperty1<Sections, *>>()
        .sortedBy { it.name.toInt() }
        .map { it.get(this) as String? }
        .filter { !it.isNullOrEmpty() }
}

fun SectionDetails.toList(): List<HadithSectionDetails?> {
    return this::class.members
        .filterIsInstance<kotlin.reflect.KProperty1<SectionDetails, *>>()
        .sortedBy { it.name.toInt() }
        .map { it.get(this) as HadithSectionDetails? }
        .filterNot { it != null && it.hadithnumber_first == 0f && it.hadithnumber_last == 0f }
}

fun KProperty1<HadithSectionDetails, *>.getPropertyName() : String{
    return when(this.name){
        "hadithnumber_first" -> "Starts at page"
        "hadithnumber_last" -> "Ends at page"
        else -> ""
    }
}

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings_prefs")


enum class ResourceType{
    VECTOR,
    PAINTER
}

data class ScreenData(
    val title: String,
    val iconResourceType : ResourceType,
    val iconRoute: Int = 0,
    val painterRoute : Int? = null,
    val route: String,
    val arguments : List<NamedNavArgument> = emptyList()
)

