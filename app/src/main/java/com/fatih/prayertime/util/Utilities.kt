package com.fatih.prayertime.util

import android.app.Application
import android.content.Context
import android.content.res.AssetManager
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavController
import com.fatih.prayertime.data.remote.dto.duadto.DuaCategory
import com.fatih.prayertime.data.remote.dto.hadithdto.HadithCollection
import com.fatih.prayertime.data.remote.dto.hadithdto.HadithSections
import com.fatih.prayertime.data.remote.dto.hadithdto.Sections
import com.fatih.prayertime.data.remote.dto.hadithdto.HadithSectionInfo
import com.fatih.prayertime.domain.model.EsmaulHusna
import com.fatih.prayertime.domain.model.PrayTimes
import com.fatih.prayertime.domain.model.HadithSectionCardData
import com.fatih.prayertime.domain.use_case.formatted_use_cases.FormattedUseCase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlin.reflect.KProperty1


fun NavController.navigateToScreen(route: String) {
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

fun convertJsonToDuaCategory(jsonString: String): DuaCategory {
    val gson = Gson()
    val duaCategoriesType = object : TypeToken<DuaCategory>() {}.type
    return gson.fromJson(jsonString, duaCategoriesType)
}


fun convertJsonToEsmaulHusnaList(jsonString: String): List<EsmaulHusna> {
    val gson = Gson()
    val listType = object : TypeToken<List<EsmaulHusna>>() {}.type
    return gson.fromJson(jsonString, listType)
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

enum class FavoritesType{
    DUA,HADIS
}

