package com.fatih.namazvakitleri.data.repository

import com.fatih.namazvakitleri.data.remote.PrayApi
import com.fatih.namazvakitleri.data.remote.dto.DailyPrayResponseDTO
import com.fatih.namazvakitleri.domain.repository.PrayRepository
import com.fatih.namazvakitleri.util.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import java.lang.Exception
import javax.inject.Inject

class PrayRepositoryInterface @Inject constructor(private val prayApi : PrayApi) : PrayRepository {

    override suspend fun getPrayTimes(date : String,latitude : Double, longitude : Double): Resource<DailyPrayResponseDTO> =
        withContext(Dispatchers.IO)  {
        return@withContext try {
            val response = prayApi.getDailyPrayTimes(date = date,latitude = latitude, longitude = longitude)
            if (response.isSuccessful) {
                response.body()?.let {
                    Resource.success(it)
                } ?: Resource.error("Response body is null")
            } else Resource.error("Response is not successfully")
        } catch (e: IOException) {
            Resource.error("Network error: ${e.message}")
        } catch (e: HttpException) {
            Resource.error("HTTP error: ${e.code()} - ${e.message()}")
        } catch (e: Exception) {
            Resource.error("An unexpected error occurred: ${e.message}")
        }
    }

}