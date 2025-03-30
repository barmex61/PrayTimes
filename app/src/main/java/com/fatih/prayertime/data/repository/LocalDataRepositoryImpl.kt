package com.fatih.prayertime.data.repository

import android.app.Application
import android.util.Log
import com.fatih.prayertime.BuildConfig
import com.fatih.prayertime.data.remote.dto.duadto.Dua
import com.fatih.prayertime.domain.model.EsmaulHusna
import com.fatih.prayertime.domain.repository.LocalDataRepository
import com.fatih.prayertime.util.utils.AssetUtils
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalDataRepositoryImpl @Inject constructor(
    private val application: Application
) : LocalDataRepository {


    private val duaData: Dua? by lazy {
        try {
            val jsonString = AssetUtils.getJsonFromAssets("dua.json", application)
            val result = AssetUtils.convertJsonToDuaCategory(jsonString)
            result
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private val esmaulHusnaData: List<EsmaulHusna>? by lazy {
        try {
            val jsonString = AssetUtils.getJsonFromAssets("esmaul_husna.json", application)
            val result = AssetUtils.convertJsonToEsmaulHusnaList(jsonString)
            result
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override fun getDua(): Dua? = duaData

    override fun getEsmaulHusna(): List<EsmaulHusna>? = esmaulHusnaData
} 