package com.fatih.prayertime.domain.use_case.dua_use_cases.get_dua_categories

import com.fatih.prayertime.domain.repository.DuaRepository
import javax.inject.Inject

class GetDuaCategoriesUseCase @Inject constructor(private val duaRepository: DuaRepository) {
    suspend operator fun invoke() = duaRepository.getDuaCategories()
}