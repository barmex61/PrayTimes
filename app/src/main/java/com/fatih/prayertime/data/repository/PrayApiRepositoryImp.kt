package com.fatih.prayertime.data.repository

import android.util.Log
import com.fatih.prayertime.data.remote.PrayApi
import com.fatih.prayertime.data.remote.dto.praytimesdto.MonthlyPrayTimesResponseDTO
import com.fatih.prayertime.domain.repository.PrayApiRepository
import com.fatih.prayertime.util.model.state.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import retrofit2.HttpException
import java.io.IOException
import java.lang.Exception
import javax.inject.Inject

class PrayApiRepositoryImp @Inject constructor(private val prayApi : PrayApi) : PrayApiRepository {

    override suspend fun getMonthlyPrayTimes(
        year: Int, 
        month: Int, 
        latitude: Double, 
        longitude: Double,
        method: Int? ,
        adjustments: String? ,
        tuneString: String?,
        school: Int,
        midnightMode: Int
    ): Resource<MonthlyPrayTimesResponseDTO> = withContext(Dispatchers.IO) {
        return@withContext try {
            withTimeout(4000L){
                val response = prayApi.getMonthlyPrayTimes(
                    year, 
                    month, 
                    latitude, 
                    longitude,
                    method,
                    adjustments,
                    tuneString,
                    school,
                    midnightMode
                )
                Log.d("PrayApiRepository","${response.raw()}")
                if (response.isSuccessful) {
                    response.body()?.let {
                        Log.d("PrayApiRepository", "Response body: $it")
                        Resource.success(it)
                    } ?: Resource.error("Response body is null")
                } else Resource.error("Response is not successfully")
            }
        } catch (e: IOException) {
            Resource.error("Network error: ${e.message}")
        } catch (e: HttpException) {
            Resource.error("HTTP error: ${e.code()} - ${e.message()}")
        } catch (e: Exception) {
            Resource.error("An unexpected error occurred: ${e.message}")
        }catch (e: TimeoutCancellationException){
            Resource.error("Timeout error: ${e.message}")
        }
    }

}