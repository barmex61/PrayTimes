package com.fatih.prayertime.domain.use_case.dua_use_cases.get_dua_details

import com.fatih.prayertime.domain.repository.DuaRepository
import javax.inject.Inject

class GetDuaDetailsUseCase @Inject constructor(private val duaRepository: DuaRepository) {
    suspend operator fun invoke(path : String) = duaRepository.getDuaCategoryDetail(path)
}