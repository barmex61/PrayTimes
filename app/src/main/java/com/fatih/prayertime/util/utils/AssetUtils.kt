package com.fatih.prayertime.util.utils

import android.app.Application
import android.content.res.AssetManager
import com.fatih.prayertime.data.remote.dto.duadto.DuaCategory
import com.fatih.prayertime.domain.model.EsmaulHusna
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object AssetUtils {
    fun getJsonFromAssets(fileName: String, application: Application): String {
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
} 