package com.fatih.prayertime.util.utils

import android.app.Application
import android.content.res.AssetManager
import android.util.Log
import com.fatih.prayertime.BuildConfig
import com.fatih.prayertime.data.remote.dto.duadto.Dua
import com.fatih.prayertime.domain.model.EsmaulHusna
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.io.IOException

object AssetUtils {
    private const val TAG = "AssetUtils"
    
    fun getJsonFromAssets(fileName: String, application: Application): String {
        try {
            val assetManager: AssetManager = application.assets
            val inputStream = assetManager.open(fileName)
            val result = inputStream.bufferedReader().use { it.readText() }

            return result
        } catch (e: IOException) {
            e.printStackTrace()
            throw e
        }
    }

    fun convertJsonToDuaCategory(jsonString: String): Dua {
        try {
            val gson = createGson()
            val duaCategoriesType = object : TypeToken<Dua>() {}.type
            val result = gson.fromJson<Dua>(jsonString, duaCategoriesType)
            return result
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }

    fun convertJsonToEsmaulHusnaList(jsonString: String): List<EsmaulHusna> {
        try {
            val gson = createGson()
            val listType = object : TypeToken<List<EsmaulHusna>>() {}.type
            val result = gson.fromJson<List<EsmaulHusna>>(jsonString, listType)

            return result
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }
    
    private fun createGson(): Gson {
        return GsonBuilder()
            .setLenient()
            .create()
    }
} 