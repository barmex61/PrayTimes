package com.fatih.namazvakitleri.data.remote

import com.fatih.namazvakitleri.data.remote.dto.DailyPrayResponseDTO
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PrayApi {
 //https://api.aladhan.com/v1/timings/05-01-2025?latitude=40.9534728&longitude=39.9312456
    @GET("{searchPath}/{date}")
    suspend fun getDailyPrayTimes(
     @Path("searchPath") searchPath : String = "timings",
     @Path("date") date : String,
     @Query("latitude") latitude : Double,
     @Query("longitude") longitude : Double
    ) : Response<DailyPrayResponseDTO>
}