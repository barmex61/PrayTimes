package com.fatih.prayertime.util

import android.app.Application
import android.content.Context
import android.content.res.AssetManager
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.fatih.prayertime.domain.model.EsmaulHusna
import com.fatih.prayertime.domain.model.PrayTimes
import com.fatih.prayertime.domain.use_case.formatted_use_cases.FormattedUseCase
import org.json.JSONArray


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
    val route: String
)

