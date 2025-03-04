package com.fatih.prayertime.data.remote

import com.fatih.prayertime.data.remote.dto.hadithdto.HadithCollection
import com.fatih.prayertime.data.remote.dto.hadithdto.HadithEdition
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface HadithApi {

    @GET("editions.json")
    suspend fun getHadithEditions() : Response<HadithEdition>
    @GET("editions/{collectionPath}")
    suspend fun getHadithCollections(@Path("collectionPath") collectionPath : String) : Response<HadithCollection>
}