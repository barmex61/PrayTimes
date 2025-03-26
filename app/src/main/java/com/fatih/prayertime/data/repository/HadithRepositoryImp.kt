package com.fatih.prayertime.data.repository

import androidx.compose.runtime.collectAsState
import com.fatih.prayertime.data.remote.HadithApi
import com.fatih.prayertime.data.remote.dto.hadithdto.HadithCollection
import com.fatih.prayertime.data.remote.dto.hadithdto.HadithEdition
import com.fatih.prayertime.domain.repository.HadithRepository
import com.fatih.prayertime.util.model.state.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import retrofit2.HttpException
import java.io.IOException
import java.lang.Exception
import javax.inject.Inject

class HadithRepositoryImp @Inject constructor(private val hadithApi: HadithApi) : HadithRepository {

    override fun getHadithEditions(): Flow<Resource<HadithEdition>> = flow {
        try {
            withTimeout(5000){
                val response = hadithApi.getHadithEditions()
                if (response.isSuccessful) {
                    response.body()?.let {
                        emit(Resource.success(it))
                    } ?: emit(Resource.error("An unknown error occurred ${response.message()}"))
                } else {
                    emit(Resource.error("An unknown error occurred ${response.message()}"))
                }
            }
        }catch (e: IOException) {
            emit(Resource.error("Network error: ${e.message}"))
        } catch (e: HttpException) {
            emit(Resource.error("HTTP error: ${e.code()} - ${e.message()}"))
        } catch (e: Exception) {
            emit(Resource.error("An unexpected error ss occurred: ${e.message}"))
        }catch (e:TimeoutCancellationException){
            emit(Resource.error("Timeout error: ${e.message}"))
        }
    }.flowOn(Dispatchers.IO)

    override fun getHadithCollections(collectionPath : String): Flow<Resource<HadithCollection>> = flow {

        val transformedPath = collectionPath.substringAfterLast("/")
        try {
            withTimeout(5000) {
                val response = hadithApi.getHadithCollections(transformedPath)
                if (response.isSuccessful) {
                    response.body()?.let {
                        emit(Resource.success(it))
                    } ?: emit(Resource.error("An unknown error occurred ${response.message()}"))
                } else {
                    emit(Resource.error("An unknown error occurred ${response.message()}"))
                }
            }
        } catch (e: TimeoutCancellationException) {
            emit(Resource.error("Request timed out.You can try refreshing the page"))
        } catch (e: IOException) {
            emit(Resource.error("Network error: ${e.message}"))
        } catch (e: HttpException) {
            emit(Resource.error("HTTP error: ${e.code()} - ${e.message()}"))
        } catch (e: Exception) {
            emit(Resource.error("An unexpected error occurred: ${e.message}"))
        }

    }.flowOn(Dispatchers.IO)
}