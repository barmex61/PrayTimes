package com.fatih.prayertime.domain.repository

import com.fatih.prayertime.data.remote.dto.duadto.DuaCategory
import com.fatih.prayertime.domain.model.EsmaulHusna
import kotlinx.coroutines.flow.Flow

interface LocalDataRepository {
    suspend fun loadDuaCategory(): DuaCategory
    suspend fun loadEsmaulHusna(): List<EsmaulHusna>
    fun getDuaCategory(): Flow<DuaCategory?>
    fun getEsmaulHusna(): Flow<List<EsmaulHusna>>
} 