package com.fatih.prayertime.domain.repository

import com.fatih.prayertime.data.remote.dto.qurandto.SurahInfo
import com.fatih.prayertime.data.remote.dto.qurandto.QuranApiData
import com.fatih.prayertime.domain.model.JuzInfo
import com.fatih.prayertime.util.model.state.Resource
import kotlinx.coroutines.flow.Flow
import java.io.File

interface QuranApiRepository {
    suspend fun getSurahList(): Resource<List<SurahInfo>>
    suspend fun getJuzList() : Resource<List<JuzInfo>>
    suspend fun getTranslationList() : Resource<List<QuranApiData>>
    suspend fun getLanguageList() : Resource<List<String>>
    suspend fun getAudioList() : Resource<List<QuranApiData>>
    suspend fun getSelectedSurah(surahNumber : Int, surahPath : String) : Resource<SurahInfo>
    suspend fun downloadAudio(  audioPath: String,
                                reciter: String,
                                number: String,
                                shouldCache: Boolean): Flow<Resource<File>>
}