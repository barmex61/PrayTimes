package com.fatih.prayertime.data.repository

import com.fatih.prayertime.data.remote.HadithApi
import com.fatih.prayertime.data.remote.dto.hadithdto.HadithCollection
import com.fatih.prayertime.data.remote.dto.hadithdto.HadithEdition
import com.fatih.prayertime.domain.repository.HadithRepository
import com.fatih.prayertime.util.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import retrofit2.HttpException
import java.io.IOException
import java.lang.Exception
import javax.inject.Inject

class HadithRepositoryImp @Inject constructor(private val hadithApi: HadithApi) : HadithRepository {

    override suspend fun getHadithEditions(): Resource<HadithEdition> = withContext(Dispatchers.IO) {
        return@withContext try {
            val response = hadithApi.getHadithEditions()
            if (response.isSuccessful) {
                response.body()?.let {
                    Resource.success(it)
                } ?: Resource.error("An unknown error occurred ${response.message()}")
            } else {
                Resource.error("An unknown error occurred ${response.message()}")
            }
        }catch (e: IOException) {
            Resource.error("Network error: ${e.message}")
        } catch (e: HttpException) {
            Resource.error("HTTP error: ${e.code()} - ${e.message()}")
        } catch (e: Exception) {
            Resource.error("An unexpected error ss occurred: ${e.message}")
        }
    }

    override suspend fun getHadithCollections(collectionPath : String): Resource<HadithCollection> = withContext(Dispatchers.IO) {
        val transformedPath = collectionPath.substringAfterLast("/")
        return@withContext try {
            withTimeout(5000) { // 5 seconds timeout
                val response = hadithApi.getHadithCollections(transformedPath)
                if (response.isSuccessful) {
                    response.body()?.let {
                        Resource.success(it)
                    } ?: Resource.error("An unknown error occurred ${response.message()}")
                } else {
                    Resource.error("An unknown error occurred ${response.message()}")
                }
            }
        } catch (e: TimeoutCancellationException) {
            Resource.error("Request timed out.You can try refreshing the page")
        } catch (e: IOException) {
            Resource.error("Network error: ${e.message}")
        } catch (e: HttpException) {
            Resource.error("HTTP error: ${e.code()} - ${e.message()}")
        } catch (e: Exception) {
            Resource.error("An unexpected error occurred: ${e.message}")
        }

    }
}