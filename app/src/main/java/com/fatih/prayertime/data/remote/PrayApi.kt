package com.fatih.prayertime.data.remote

import com.fatih.prayertime.data.remote.dto.praytimesdto.DailyPrayResponseDTO
import com.fatih.prayertime.data.remote.dto.praytimesdto.MonthlyPrayTimesResponseDTO
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PrayApi {
 //https://api.aladhan.com/v1/timings/05-01-2025?latitude=40.9534728&longitude=39.9312456&method=2&adjustment=1
    @GET("{searchPath}/{date}")
    suspend fun getDailyPrayTimes(
     @Path("searchPath") searchPath : String = "timings",
     @Path("date") date : String,
     @Query("latitude") latitude : Double,
     @Query("longitude") longitude : Double,
     @Query("method") method: Int? = null, // Null olursa API konuma göre otomatik belirleyecek
     @Query("adjustments") adjustments: String? = null, // Örnek: "0,1,1,1,1" - Fajr,Dhuhr,Asr,Maghrib,Isha için offset değerleri
     @Query("tune") tuneString: String? = null, // Dakika bazlı hassas ayarlar
     @Query("school") school: Int = 0, // Asr vakti için mezhep (0: Hanefi, 1: Şafi)
     @Query("midnightMode") midnightMode: Int = 0 // Gece yarısı hesaplama (0: Standard, 1: Jafari)
    ) : Response<DailyPrayResponseDTO>

   // https://api.aladhan.com/v1/calendar/2025/1?latitude=40.9518184&longitude=39.9374072&method=2&adjustment=1
    @GET("calendar/{year}/{month}")
    suspend fun getMonthlyPrayTimes(
        @Path("year") year : Int,
        @Path("month") month : Int,
        @Query("latitude") latitude : Double,
        @Query("longitude") longitude : Double,
        @Query("method") method: Int? = null, // Null olursa API konuma göre otomatik belirleyecek
        @Query("adjustments") adjustments: String? = null, // Örnek: "0,1,1,1,1" - Fajr,Dhuhr,Asr,Maghrib,Isha için offset değerleri
        @Query("tune") tuneString: String? = null, // Dakika bazlı hassas ayarlar
        @Query("school") school: Int = 0, // Asr vakti için mezhep (0: Hanefi, 1: Şafi)
        @Query("midnightMode") midnightMode: Int = 0 // Gece yarısı hesaplama (0: Standard, 1: Jafari)
    ) : Response<MonthlyPrayTimesResponseDTO>
}