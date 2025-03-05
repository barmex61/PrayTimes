package com.fatih.prayertime.domain.use_case.dua_use_cases

import com.fatih.prayertime.domain.repository.DuaRepository
import javax.inject.Inject

class GetDuaDetailUseCase @Inject constructor(private val duaRepository: DuaRepository) {
    suspend operator fun invoke(path:String ,id: Int) = duaRepository.getDuaDetail(path,id)
}