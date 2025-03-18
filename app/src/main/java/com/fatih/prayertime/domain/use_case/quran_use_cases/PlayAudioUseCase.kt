package com.fatih.prayertime.domain.use_case.quran_use_cases

import com.fatih.prayertime.data.remote.dto.qurandto.QuranApiResponse
import com.fatih.prayertime.domain.repository.QuranApiRepository
import com.fatih.prayertime.util.model.state.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.File
import javax.inject.Inject

class PlayAudioUseCase @Inject constructor(
    private val repository: QuranApiRepository
) {
    suspend operator fun invoke(audioUrl: String,shouldCacheAudio : Boolean): Flow<Resource<File>> {
        val cachedFile = repository.getCachedAudioFile(audioUrl)
        
        return if (cachedFile != null) {
            flow { emit(Resource.success(cachedFile)) }
        } else {
            repository.downloadAudioFile(audioUrl,shouldCacheAudio)
        }
    }
} 