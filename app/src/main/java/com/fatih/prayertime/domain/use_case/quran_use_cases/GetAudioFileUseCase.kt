package com.fatih.prayertime.domain.use_case.quran_use_cases

import com.fatih.prayertime.domain.repository.QuranApiRepository
import com.fatih.prayertime.util.model.state.Resource
import kotlinx.coroutines.flow.Flow
import java.io.File
import javax.inject.Inject

class GetAudioFileUseCase @Inject constructor(
    private val repository: QuranApiRepository
) {
    suspend operator fun invoke(audioUrl: String,shouldCacheAudio : Boolean): Flow<Resource<File>> {
        return repository.downloadAudio(audioUrl,shouldCacheAudio)

    }
} 