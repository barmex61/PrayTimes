package com.fatih.prayertime.domain.use_case.dua_use_case

import com.fatih.prayertime.domain.repository.LocalDataRepository
import javax.inject.Inject

class GetDuaUseCase @Inject constructor(private val localDataRepository : LocalDataRepository){

    suspend operator fun invoke() = localDataRepository.getDua()
}