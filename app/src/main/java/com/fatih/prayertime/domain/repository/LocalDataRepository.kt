package com.fatih.prayertime.domain.repository

import com.fatih.prayertime.data.remote.dto.duadto.Dua
import com.fatih.prayertime.domain.model.EsmaulHusna
import kotlinx.coroutines.flow.Flow

interface LocalDataRepository {
    suspend fun loadDua(): Dua
    suspend fun loadEsmaulHusna(): List<EsmaulHusna>
    fun getDua(): Flow<Dua?>
    fun getEsmaulHusna(): Flow<List<EsmaulHusna>>
}