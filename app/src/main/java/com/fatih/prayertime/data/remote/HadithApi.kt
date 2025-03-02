package com.fatih.prayertime.data.remote

import com.fatih.prayertime.data.remote.dto.hadithdto.HadithEdition
import retrofit2.Response
import retrofit2.http.GET

interface HadithApi {

    @GET("editions.json")
    suspend fun getHadithEditions() : Response<HadithEdition>
}