package com.fatih.prayertime.domain.repository

import com.fatih.prayertime.data.remote.dto.duadto.DuaCategories
import com.fatih.prayertime.data.remote.dto.duadto.DuaCategoryDetail
import com.fatih.prayertime.util.Resource

interface DuaRepository {

    suspend fun getDuaCategories() : Resource<DuaCategories>
    suspend fun getDuaCategoryDetail(path : String) : Resource<DuaCategoryDetail>
}