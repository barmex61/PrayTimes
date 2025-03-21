package com.fatih.prayertime.data.remote

import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Streaming

interface AudioApi {

    @GET("{audioPath}/{bitrate}/{reciter}/{number}.mp3")
    @Streaming
    suspend fun downloadAudio(
        @Path("audioPath") audioPath: String,
        @Path("bitrate") bitrate : Int,
        @Path("reciter") reciter: String,
        @Path("number") number: String
    ): ResponseBody
}