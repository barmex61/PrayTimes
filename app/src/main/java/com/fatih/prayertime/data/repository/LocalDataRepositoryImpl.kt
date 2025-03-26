package com.fatih.prayertime.data.repository

import android.app.Application
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
            AssetUtils.convertJsonToDuaCategory(jsonString)
        }catch (_: Exception){
            null
        }

    }

    private val esmaulHusnaData: List<EsmaulHusna>? by lazy {
        try {
            val jsonString = AssetUtils.getJsonFromAssets("esmaul_husna.json", application)
            AssetUtils.convertJsonToEsmaulHusnaList(jsonString)
        }catch (_: Exception){
            null
        }

    }

    override fun getDua(): Dua? = duaData

    override fun getEsmaulHusna(): List<EsmaulHusna>? = esmaulHusnaData


} 