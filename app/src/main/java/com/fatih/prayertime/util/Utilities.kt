package com.fatih.prayertime.util

import android.app.Activity
import android.app.Application
import android.content.pm.ActivityInfo
import android.content.res.AssetManager
import android.graphics.drawable.GradientDrawable.Orientation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalContext
import com.fatih.prayertime.domain.model.EsmaulHusna
import org.json.JSONArray

@Composable
fun LockScreenOrientation(orientation: Int){
    val context = LocalContext.current
    val activity = (context as? Activity)
    DisposableEffect (key1 = Unit){
        activity?.requestedOrientation = orientation
        onDispose {
            activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        }
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