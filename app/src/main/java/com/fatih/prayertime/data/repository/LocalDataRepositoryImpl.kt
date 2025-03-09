package com.fatih.prayertime.data.repository

import android.app.Application
import com.fatih.prayertime.data.remote.dto.duadto.Dua
import com.fatih.prayertime.domain.model.EsmaulHusna
import com.fatih.prayertime.domain.repository.LocalDataRepository
import com.fatih.prayertime.util.utils.AssetUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalDataRepositoryImpl @Inject constructor(
    private val application: Application
) : LocalDataRepository {

    private val duaCategoryFlow = MutableStateFlow<Dua?>(null)
    private val esmaulHusnaFlow = MutableStateFlow<List<EsmaulHusna>>(emptyList())

    override suspend fun loadDua(): Dua {
        val jsonString = AssetUtils.getJsonFromAssets("dua.json", application)
        val duaCategory = AssetUtils.convertJsonToDuaCategory(jsonString)
        duaCategoryFlow.emit(duaCategory)
        return duaCategory
    }

    override suspend fun loadEsmaulHusna(): List<EsmaulHusna> {
        val jsonString = AssetUtils.getJsonFromAssets("esmaul_husna.json", application)
        val esmaulHusnaList = AssetUtils.convertJsonToEsmaulHusnaList(jsonString)
        esmaulHusnaFlow.emit(esmaulHusnaList)
        return esmaulHusnaList
    }

    override fun getDua(): Flow<Dua?> = duaCategoryFlow

    override fun getEsmaulHusna(): Flow<List<EsmaulHusna>> = esmaulHusnaFlow
} 