package com.fatih.prayertime.data.remote

import com.fatih.prayertime.data.remote.dto.qurandto.LanguageResponse
import com.fatih.prayertime.data.remote.dto.qurandto.SurahInfoListResponse
import com.fatih.prayertime.data.remote.dto.qurandto.QuranApiResponse
import com.fatih.prayertime.data.remote.dto.qurandto.SurahResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface QuranApi {

    @GET("surah")
    suspend fun getSurahList(): Response<SurahInfoListResponse>
    @GET("edition/language")
    suspend fun getLanguageList(): Response<LanguageResponse>
    @GET("edition/language/{language}")
    suspend fun getTranslationList(
        @Path("language") language: String
    ): Response<QuranApiResponse>
    @GET("edition/format/audio")
    suspend fun getAudioList(): Response<QuranApiResponse>
    @GET("surah/{surahNumber}/editions/{surahPath}")
    suspend fun getSelectedSurah(
        @Path("surahNumber") surahNumber: Int,
        @Path("surahPath") surahPath: String,
        @Header("Accept-Charset") charset: String = "UTF-8"
    ): Response<SurahResponse>

}