package com.fatih.prayertime.data.repository

import android.content.Context
import com.fatih.prayertime.data.remote.AudioApi
import com.fatih.prayertime.data.remote.CDNApi
import com.fatih.prayertime.data.remote.QuranApi
import com.fatih.prayertime.data.remote.dto.qurandto.Ayah
import com.fatih.prayertime.data.remote.dto.qurandto.SurahInfo
import com.fatih.prayertime.data.remote.dto.qurandto.QuranApiData
import com.fatih.prayertime.domain.model.JuzInfo
import com.fatih.prayertime.domain.repository.QuranApiRepository
import com.fatih.prayertime.util.model.state.Resource
import com.fatih.prayertime.util.model.state.Status
import com.fatih.prayertime.util.utils.QuranUtils.juzList
import com.fatih.prayertime.util.utils.QuranUtils.turkishNames
import com.fatih.prayertime.util.utils.QuranUtils.turkishTranslations
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
import javax.inject.Inject

class QuranApiRepositoryImp @Inject constructor(
    private val context : Context,
    private val quranApi: QuranApi,
    private val audioApi: AudioApi,
    private val cdnApi : CDNApi
) : QuranApiRepository {

    private val verseByVerseReciters = mutableListOf<QuranApiData>()
    private val surahBySurahReciters = mutableListOf<QuranApiData>()
    private val translationList = mutableListOf<QuranApiData>()
    private val mutex = Mutex()


    override suspend fun getSurahList(): Resource<List<SurahInfo>> = withContext(Dispatchers.IO) {
        return@withContext try {
            println("getsurahlist")
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
        mutex.withLock {
            if (translationList.isNotEmpty()) return@withContext Resource.success(translationList)
        }
        return@withContext try {
            mutex.withLock {
            val languageListResponse = getLanguageList()
                if (languageListResponse.status == Status.SUCCESS) {
                        translationList.clear()
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
                delay(150)
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

    override suspend fun getVerseByVerseReciters(): Resource<List<QuranApiData>> = withContext(Dispatchers.IO) {
        if (verseByVerseReciters.isNotEmpty()) return@withContext Resource.success(verseByVerseReciters)
        return@withContext try {
            val recitersResponse = quranApi.getVerseByVerseReciters()
            if (recitersResponse.isSuccessful){
                recitersResponse.body()?.let {
                    verseByVerseReciters.clear()
                    verseByVerseReciters.addAll(it.data.filter { it.language == "ar" })
                    Resource.success(verseByVerseReciters)
                }?: Resource.error("An unexpected error occurred ${recitersResponse.message()}")
            }else{
                Resource.error("An unexpected error occurred ${recitersResponse.message()}")
            }
        }catch (e: Exception){
            Resource.error(e.message)
        }
    }

    override suspend fun getSurahBySurahReciters(): Resource<List<QuranApiData>> = withContext(Dispatchers.IO) {
        if (surahBySurahReciters.isNotEmpty()) return@withContext Resource.success(surahBySurahReciters)
        return@withContext try {
            val audioResponse = cdnApi.getSurahBySurahReciters()
            if (audioResponse.isSuccessful){
                audioResponse.body()?.let {
                    surahBySurahReciters.clear()
                    surahBySurahReciters.addAll(it.filter { it.language == "ar" })
                    Resource.success(surahBySurahReciters)
                }?: Resource.error("An unexpected error occurred ${audioResponse.message()}")
            }else{
                Resource.error("An unexpected error occurred ${audioResponse.message()}")
            }
        }catch (e: Exception){
            Resource.error(e.message)
        }
    }

    override suspend fun getSelectedSurah(surahNumber : Int, surahPath : String): Resource<SurahInfo> = withContext(Dispatchers.IO) {
        return@withContext try {
            val surahResponse = quranApi.getSelectedSurah(surahNumber,surahPath)
            if (surahResponse.isSuccessful){
                val ayahList = mutableListOf<Ayah>()
                val transliterationTextList = mutableListOf<String>()
                val translationText = mutableListOf<String>()
                surahResponse.body()?.let { surahResponse ->
                    surahResponse.data.forEachIndexed { index,surahInfo ->
                        surahInfo.turkishName = turkishNames[surahInfo.englishName] ?: "Bulunamadı"
                        surahInfo.turkishNameTranslation= turkishTranslations[surahInfo.englishName] ?: "Bulunamadı"
                        when (index) {
                            0 -> ayahList.addAll(surahInfo.ayahs!!)
                            1 -> transliterationTextList.addAll(surahInfo.ayahs!!.map { it.text })
                            else -> translationText.addAll(surahInfo.ayahs!!.map { it.text })
                        }
                    }
                    ayahList.zip(transliterationTextList).forEach {
                        it.first.textTransliteration = it.second
                    }
                    ayahList.zip(translationText).forEach {
                        it.first.textTranslation = it.second
                    }
                    val returnSurahInfo = surahResponse.data[0].copy(ayahs = ayahList)
                    Resource.success(returnSurahInfo)
                }?: Resource.error("An unexpected error occurred ${surahResponse.message()}")
            }else{
                Resource.error("An unexpected error occurred ${surahResponse.message()}")
            }
        }catch (e: Exception){
            Resource.error(e.message)
        }
    }

    override suspend fun downloadAudio(
        audioPath: String,
        bitrate : Int,
        reciter: String,
        number: Int,
        shouldCache: Boolean
    ): Flow<Resource<File>> = flow {
        val fileName = "${audioPath}_${reciter}_$number.mp3"
        val cacheDir = File(context.cacheDir, "quran_audio")
        val cachedFile = File(cacheDir, fileName)

        if (cachedFile.exists()) {
            emit(Resource.success(cachedFile))
            return@flow
        }

        val outputFile = if (shouldCache) {
            if (!cacheDir.exists()) {
                cacheDir.mkdirs()
            }
            cachedFile
        } else {
            File.createTempFile("quran_audio", ".mp3", context.cacheDir)
        }

        try {
            emit(Resource.loading<File>())

            val response = audioApi.downloadAudio(audioPath, bitrate, reciter, number)
            val responseBody = response.body()
            println("response ${response.raw()}")
            if (!response.isSuccessful || responseBody == null) {
                throw IOException("İndirme başarısız oldu, kod: ${response.code()}")
            }

            val totalSize = responseBody.contentLength()
            var downloadedSize = 0L

            emit(Resource.loading(0, 0, totalSize))
            println("emit first loading")
            responseBody.byteStream().use { inputStream ->
                println("inputstream")
                outputFile.outputStream().use { outputStream ->
                    println("outputstream")
                    val buffer = ByteArray(8192)
                    var bytesRead: Int

                    while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                        println("while loop")
                        outputStream.write(buffer, 0, bytesRead)
                        downloadedSize += bytesRead
                        val progress = (downloadedSize * 100f / totalSize).toInt()
                        emit(Resource.loading(progress, downloadedSize, totalSize))
                    }
                }
            }

            emit(Resource.success(outputFile))
        } catch (e: Exception) {
            emit(Resource.error(e.localizedMessage ?: "Ses dosyası indirilemedi"))
            if (outputFile.exists()) {
                outputFile.delete()
            }
        }
    }.flowOn(Dispatchers.IO)
}