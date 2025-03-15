package com.fatih.prayertime.data.remote

import com.fatih.prayertime.data.remote.dto.qurandto.SurahResponse
import retrofit2.Response
import retrofit2.http.GET

interface QuranApi {

    @GET("surah")
    suspend fun getSuraList(): Response<SurahResponse>
}