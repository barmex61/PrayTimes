package com.fatih.prayertime.data.remote

import com.fatih.prayertime.data.remote.dto.qurandto.QuranApiData
import com.fatih.prayertime.data.remote.dto.qurandto.QuranApiResponse
import retrofit2.Response
import retrofit2.http.GET

interface CDNApi {
    @GET("islamic-network/cdn/master/info/cdn_surah_audio.json")
    suspend fun getSurahBySurahReciters(): Response<List<QuranApiData>>
}