package com.fatih.prayertime.data.remote

import com.fatih.prayertime.data.remote.dto.duadto.DuaCategories
import com.fatih.prayertime.data.remote.dto.duadto.DuaCategoryDetail
import com.fatih.prayertime.data.remote.dto.duadto.DuaDetail
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface DuaApi {

    @GET("categories")
    suspend fun getDuaCategories(@Header("Accept-Language") accept_language : String = "en") : Response<DuaCategories>
    @GET("categories/{slug}")
    suspend fun getDuaCategoryDetail(
        @Header("Accept-Language") accept_language : String = "en",
        @Path("slug") path : String
    ) : Response<DuaCategoryDetail>
    @GET("categories/{slug}/{id}")
    suspend fun getDuaDetail(
        @Header("Accept-Language") accept_language : String = "en",
        @Path("slug") path : String,
        @Path("id") id : Int
    ) : Response<DuaDetail>
}