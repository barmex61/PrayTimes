package com.fatih.prayertime.data.repository

import com.fatih.prayertime.data.remote.QuranApi
import com.fatih.prayertime.data.remote.dto.qurandto.SurahInfo
import com.fatih.prayertime.data.remote.dto.qurandto.QuranApiData
import com.fatih.prayertime.data.remote.dto.qurandto.QuranApiResponse
import com.fatih.prayertime.domain.model.JuzInfo
import com.fatih.prayertime.domain.repository.QuranApiRepository
import com.fatih.prayertime.util.model.state.Resource
import com.fatih.prayertime.util.model.state.Status
import com.fatih.prayertime.util.utils.QuranUtils.juzList
import com.fatih.prayertime.util.utils.QuranUtils.turkishNames
import com.fatih.prayertime.util.utils.QuranUtils.turkishTranslations
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import retrofit2.Response
import javax.inject.Inject

class QuranApiRepositoryImp @Inject constructor(private val quranApi: QuranApi) : QuranApiRepository {


    override suspend fun getSurahList(): Resource<List<SurahInfo>> = withContext(Dispatchers.IO) {
        return@withContext try {
            val response = quranApi.getSurahList()
            if (response.isSuccessful) {
                response.body()?.let {
                    it.data.forEach {
                        it.turkishName = turkishNames[it.englishName] ?: "Bulunamadı"
                        it.turkishNameTranslation= turkishTranslations[it.englishName] ?: "Bulunamadı"
                    }
                    Resource.success(it.data)
                } ?: Resource.error("An unexpected error occurred ${response.message()}")
            }else Resource.error("An unexpected error occurred ${response.message()}")
        } catch (e: Exception){
            Resource.error("An unexpected error occurred: ${e.message}")
        }
    }

    override suspend fun getJuzList(): Resource<List<JuzInfo>> {
        return Resource.success(juzList)
    }

    override suspend fun getTranslationList(): Resource<List<QuranApiData>> = withContext(Dispatchers.IO) {
        return@withContext try {
            val translationList = mutableListOf<QuranApiData>()

            val languageListResponse = getLanguageList()
            if (languageListResponse.status == Status.SUCCESS) {
                languageListResponse.data!!.forEach { languageCode ->
                    val result = fetchTranslationListWithRetry(languageCode)
                    if (result.status == Status.SUCCESS) {
                        translationList.addAll(result.data ?: emptyList())
                    } else {
                        return@withContext Resource.error(result.message ?: "Unknown error while fetching translations for $languageCode")
                    }
                }
                Resource.success(translationList)
            } else {
                Resource.error(languageListResponse.message ?: "Error occurred while fetching language list")
            }
        } catch (e: Exception) {
            Resource.error("Exception occurred: ${e.message}")
        }
    }

    private suspend fun fetchTranslationListWithRetry(languageCode: String): Resource<List<QuranApiData>> {
        var attempt = 0
        var lastError: String? = null

        repeat(3) {
            attempt++
            val translationResponse = quranApi.getTranslationList(languageCode)

            if (translationResponse.isSuccessful) {
                translationResponse.body()?.let { translationData ->
                    val filteredList = translationData.data.filter { it.type == "translation" && it.format != "audio" }
                    return Resource.success(filteredList)
                } ?: return Resource.error("Translation body is null for language $languageCode")
            } else {
                lastError = "Attempt $attempt failed for language $languageCode: ${translationResponse.message()}"
                println(lastError)
                delay(1000)
            }
        }
        return Resource.error(lastError ?: "Unknown error while fetching translations for $languageCode")
    }

    override suspend fun getLanguageList(): Resource<List<String>> = withContext(Dispatchers.IO) {
        return@withContext try {
            val languageListResponse = quranApi.getLanguageList()
            if (languageListResponse.isSuccessful){
                languageListResponse.body()?.let {
                    Resource.success(it.data)
                }?: Resource.error("An unexpected error occurred ${languageListResponse.message()}")
            }else{
                Resource.error("An unexpected error occurred ${languageListResponse.message()}")
            }
        }catch (e: Exception){
            Resource.error(e.message)
        }

    }

    override suspend fun getAudioList(): Resource<List<QuranApiData>> = withContext(Dispatchers.IO) {
        return@withContext try {
            val audioResponse = quranApi.getAudioList()
            if (audioResponse.isSuccessful){
                audioResponse.body()?.let {
                    Resource.success(it.data.filter { it.language == "ar" })
                }?: Resource.error("An unexpected error occurred ${audioResponse.message()}")
            }else{
                Resource.error("An unexpected error occurred ${audioResponse.message()}")
            }
        }catch (e: Exception){
            Resource.error(e.message)
        }
    }

    override suspend fun getSelectedSurah(surahNumber : Int,audioPath : String): Resource<SurahInfo> = withContext(Dispatchers.IO) {
        return@withContext try {
            val surahResponse = quranApi.getSelectedSurah(surahNumber,audioPath)
            if (surahResponse.isSuccessful){
                surahResponse.body()?.let {
                    it.data.turkishName = turkishNames[it.data.englishName] ?: "Bulunamadı"
                    it.data.turkishNameTranslation= turkishTranslations[it.data.englishName] ?: "Bulunamadı"
                    Resource.success(it.data)
                }?: Resource.error("An unexpected error occurred ${surahResponse.message()}")
            }else{
                Resource.error("An unexpected error occurred ${surahResponse.message()}")
            }
        }catch (e: Exception){
            Resource.error(e.message)
        }
    }

}