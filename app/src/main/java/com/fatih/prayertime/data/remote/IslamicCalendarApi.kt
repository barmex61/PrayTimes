package com.fatih.prayertime.data.remote

import com.fatih.prayertime.data.remote.dto.islamicdaysdto.IslamicDaysDTO
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface IslamicCalendarApi {

    @GET("gToHCalendar/{month}/{year}")
    suspend fun getIslamicDays(
        @Path("month") month : Int,
        @Path("year") year : Int,
        @Query("calendarMethod") calendarMethod : String,
    ) : Response<IslamicDaysDTO>
}