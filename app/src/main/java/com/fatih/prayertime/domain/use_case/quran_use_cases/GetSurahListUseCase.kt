package com.fatih.prayertime.domain.use_case.quran_use_cases

import com.fatih.prayertime.domain.repository.QuranApiRepository
import com.fatih.prayertime.util.model.state.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class GetSurahListUseCase @Inject constructor(private val quranApiRepository: QuranApiRepository) {
    operator fun invoke() = flow {
        emit(Resource.loading())
        try {
            emit(quranApiRepository.getSurahList())
        }catch (e: Exception){
            emit(Resource.error(e.message))
        }
    }.flowOn(Dispatchers.IO)
}