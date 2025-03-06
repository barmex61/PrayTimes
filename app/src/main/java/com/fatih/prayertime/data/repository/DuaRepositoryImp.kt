package com.fatih.prayertime.data.repository

import android.util.Log
import com.fatih.prayertime.data.remote.DuaApi
import com.fatih.prayertime.data.remote.dto.duadto.DuaCategories
import com.fatih.prayertime.data.remote.dto.duadto.DuaCategoryDetail
import com.fatih.prayertime.data.remote.dto.duadto.DuaDetail
import com.fatih.prayertime.domain.repository.DuaRepository
import com.fatih.prayertime.util.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class DuaRepositoryImp @Inject constructor(private val duaApi: DuaApi) : DuaRepository {

    override suspend fun getDuaCategories(): Resource<DuaCategories> = withContext(Dispatchers.IO) {
        return@withContext try {
            withTimeout(4000L){
                val response = duaApi.getDuaCategories()
                if (response.isSuccessful) {
                    response.body()?.let {
                        Resource.success(it)
                    } ?: Resource.error("Response body is null")
                } else {
                    Resource.error(response.message())
                }
            }
        } catch (e: IOException) {
            Resource.error("Network error: ${e.message}")
        } catch (e: HttpException) {
            Resource.error("HTTP error: ${e.code()} - ${e.message()}")
        } catch (e: Exception) {
            Resource.error("An unexpected error ss occurred: ${e.message}")
        } catch (e:TimeoutCancellationException){
            Resource.error("Timeout error: ${e.message}", exception = e)
        }
    }

    override suspend fun getDuaCategoryDetail(path : String): Resource<DuaCategoryDetail> = withContext(Dispatchers.IO) {
        return@withContext try {
            withTimeout(4000L){
                val response = duaApi.getDuaCategoryDetail(path = path)
                if (response.isSuccessful) {
                    response.body()?.let {
                        Resource.success(it)
                    } ?: Resource.error("Response body is null")
                } else {
                    Resource.error(response.message())
                }
            }
        } catch (e: IOException) {
            Resource.error("Network error: ${e.message}")
        } catch (e: HttpException) {
            Resource.error("HTTP error: ${e.code()} - ${e.message()}")
        } catch (e: Exception) {
            Resource.error("An unexpected error ss occurred: ${e.message}")
        }catch (e:TimeoutCancellationException){
            Resource.error("Timeout error: ${e.message}", exception = e)
        }
    }

    override suspend fun getDuaDetail(path:String,id: Int): Resource<DuaDetail> = withContext(Dispatchers.IO){
        return@withContext try {
            withTimeout(4000L){
                val response = duaApi.getDuaDetail(path = path,id = id)
                if (response.isSuccessful) {
                    response.body()?.let {
                        Resource.success(it)
                    } ?: Resource.error("Response body is null")
                } else {
                    Resource.error(response.message())
                }
            }
        } catch (e: IOException) {
            Resource.error("Network error: ${e.message}")
        } catch (e: HttpException) {
            Resource.error("HTTP error: ${e.code()} - ${e.message()}")
        } catch (e: Exception) {
            Resource.error("An unexpected error ss occurred: ${e.message}")
        }catch (e:TimeoutCancellationException){
            Resource.error("Timeout error: ${e.message}", exception = e)
        }
    }

}