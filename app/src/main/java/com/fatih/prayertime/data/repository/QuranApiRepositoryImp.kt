package com.fatih.prayertime.data.repository

import com.fatih.prayertime.data.remote.QuranApi
import com.fatih.prayertime.data.remote.dto.qurandto.SurahInfo
import com.fatih.prayertime.data.remote.dto.qurandto.SurahResponse
import com.fatih.prayertime.domain.model.JuzInfo
import com.fatih.prayertime.domain.repository.QuranApiRepository
import com.fatih.prayertime.util.model.state.Resource
import com.fatih.prayertime.util.utils.QuranUtils.juzList
import javax.inject.Inject

class QuranApiRepositoryImp @Inject constructor(private val quranApi: QuranApi) : QuranApiRepository {


    override suspend fun getSurahList(): Resource<List<SurahInfo>> {
        return try {
            val response = quranApi.getSuraList()
            if (response.isSuccessful) {
                response.body()?.let {
                    return Resource.success(it.data)
                } ?: Resource.error("An unexpected error occurred ${response.message()}")
            }else Resource.error("An unexpected error occurred ${response.message()}")
        } catch (e: Exception){
            Resource.error("An unexpected error occurred: ${e.message}")
        }
    }

    override suspend fun getJuzList(): Resource<List<JuzInfo>> {
        return Resource.success(juzList)
    }
}