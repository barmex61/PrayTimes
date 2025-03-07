package com.fatih.prayertime.util

import android.app.Application
import android.content.Context
import android.content.res.AssetManager
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.text.toLowerCase
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavController
import com.fatih.prayertime.R
import com.fatih.prayertime.data.remote.dto.duadto.DuaCategories
import com.fatih.prayertime.data.remote.dto.duadto.DuaCategoryData
import com.fatih.prayertime.data.remote.dto.hadithdto.HadithCollection
import com.fatih.prayertime.data.remote.dto.hadithdto.HadithSections
import com.fatih.prayertime.data.remote.dto.hadithdto.Sections
import com.fatih.prayertime.data.remote.dto.hadithdto.HadithSectionInfo
import com.fatih.prayertime.domain.model.EsmaulHusna
import com.fatih.prayertime.domain.model.PrayTimes
import com.fatih.prayertime.domain.model.HadithSectionCardData
import com.fatih.prayertime.domain.model.PrayCategoryResponseTr
import com.fatih.prayertime.domain.model.PrayCategoryTr
import com.fatih.prayertime.domain.use_case.formatted_use_cases.FormattedUseCase
import com.google.gson.Gson
import org.json.JSONArray
import java.io.InputStreamReader
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import kotlin.reflect.KProperty1


fun NavController.navigateToScreen( screenData: ScreenData, path : String? = null) {
    println(screenData)
    val route = if (path != null) {
        val encodedUrl = URLEncoder.encode(path, StandardCharsets.UTF_8.toString())
        screenData.route.replace("{collectionPath}", encodedUrl)
    } else {
        screenData.route
    }
    this.navigate(route) {
        popUpTo(this@navigateToScreen.graph.startDestinationId) { saveState = false }
        launchSingleTop = true
        restoreState = false
        if (route == "qibla") this@navigateToScreen.popBackStack()
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

fun loadDuaCategories(context: Context): List<PrayCategoryTr> {
    val inputStream = context.assets.open("duaCategoryTr.json")
    val reader = InputStreamReader(inputStream)
    val response = Gson().fromJson(reader, PrayCategoryResponseTr::class.java)
    return response.data
}

fun DuaCategories.addTrSupport() : DuaCategories {
    return this.apply {
        this.data.forEachIndexed { index, duaCategoryData ->
            duaCategoryData.nameTr = Constants.prayCategoryTr[index].nameTr
        }
    }
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

fun combineSectionsAndDetails(hadithCollection: HadithCollection): List<HadithSectionCardData> {
    val sectionList = hadithCollection.metadata.sections.toList()
    val detailsList = hadithCollection.metadata.section_details.toList()

    return sectionList.zip(detailsList) { section, details ->
        val subHadithList = hadithCollection.hadiths.subList(details?.hadithnumber_first.anyToInt()!! - 1, details?.hadithnumber_last.anyToInt()!! )
        HadithSectionCardData(section, details,subHadithList,subHadithList.size)
    }
}

fun Sections.toList(): List<String?> {
    return this::class.members
        .filterIsInstance<KProperty1<Sections, *>>()
        .sortedBy { it.name.toInt() }
        .map { it.get(this) as String? }
        .filter { !it.isNullOrEmpty() }
}

fun HadithSections.toList(): List<HadithSectionInfo?> {
    return this::class.members
        .filterIsInstance<KProperty1<HadithSections, *>>()
        .sortedBy { it.name.toInt() }
        .map { it.get(this) as HadithSectionInfo? }
        .filterNot { it != null && it.hadithnumber_first == 0f && it.hadithnumber_last == 0f }
}


fun KProperty1<HadithSectionInfo, *>.getPropertyName() : String{
    return when(this.name){
        "hadithnumber_first" -> "Starts at hadith no"
        "hadithnumber_last" -> "Ends at hadith no"
        else -> ""
    }
}

fun Any?.anyToInt() : Int? {
    return try {
        (this as Float).toInt()
    }catch (e:Exception){
        null
    }
}

fun String.capitalizeFirstLetter(): String {
    return this.replaceFirstChar {
        if (it.isLowerCase()) it.titlecase() else it.toString()
    }
}

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings_prefs")


enum class ResourceType{
    VECTOR,
    PAINTER
}

data class ScreenData(
    val title: PrayTimesString,
    val iconResourceType : ResourceType,
    val iconRoute: Int = 0,
    val painterRoute : Int? = null,
    val route: String,
    val arguments : List<NamedNavArgument> = emptyList()
)

