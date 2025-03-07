package com.fatih.prayertime.domain.repository

import com.fatih.prayertime.data.remote.dto.duadto.DuaCategory
import com.fatih.prayertime.data.remote.dto.duadto.DuaCategoryDetail
import com.fatih.prayertime.data.remote.dto.duadto.DuaDetail
import com.fatih.prayertime.util.Resource

interface DuaRepository {

    suspend fun getDuaCategories() : Resource<DuaCategory>
    suspend fun getDuaCategoryDetail(path : String) : Resource<DuaCategoryDetail>
    suspend fun getDuaDetail(path:String,id : Int) : Resource<DuaDetail>
}