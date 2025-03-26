package com.fatih.prayertime.domain.repository

import com.fatih.prayertime.data.remote.dto.duadto.Dua
import com.fatih.prayertime.domain.model.EsmaulHusna
import kotlinx.coroutines.flow.Flow

interface LocalDataRepository {

    fun getDua(): Dua?
    fun getEsmaulHusna(): List<EsmaulHusna>?
}