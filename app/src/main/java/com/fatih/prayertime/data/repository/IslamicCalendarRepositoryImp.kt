package com.fatih.prayertime.data.repository

import com.fatih.prayertime.data.remote.IslamicCalendarApi
import com.fatih.prayertime.data.remote.dto.islamicdaysdto.IslamicDaysDataDTO
import com.fatih.prayertime.domain.repository.IslamicCalendarRepository
import com.fatih.prayertime.util.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import java.lang.Exception
import javax.inject.Inject

class IslamicCalendarRepositoryImp @Inject constructor(private val islamicCalendarApi : IslamicCalendarApi) : IslamicCalendarRepository {

    override suspend fun getIslamicCalendarForMonth(month : Int , year : Int,calendarMethod : String): Resource<List<IslamicDaysDataDTO>> = withContext(Dispatchers.IO) {
        return@withContext try {
            val apiResponse = islamicCalendarApi.getIslamicDays(month,year,calendarMethod)
            if (apiResponse.isSuccessful){
                apiResponse.body()?.let {
                   Resource.success(it.islamicDaysDatumDTOS)
                }?:Resource.error("Response body is null")
            }else{
                Resource.error("Response is not successfully")
            }
        }catch (e: IOException) {
            Resource.error("Network error: ${e.message}")
        } catch (e: HttpException) {
            Resource.error("HTTP error: ${e.code()} - ${e.message()}")
        } catch (e: Exception) {
            Resource.error("An unexpected error ss occurred: ${e.message}")
        }catch (e: TimeoutCancellationException){
            Resource.error("Timeout error: ${e.message}")
        }
    }
}