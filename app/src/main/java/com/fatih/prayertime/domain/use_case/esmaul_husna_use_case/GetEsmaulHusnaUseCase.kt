package com.fatih.prayertime.domain.use_case.esmaul_husna_use_case

import com.fatih.prayertime.data.repository.LocalDataRepositoryImpl
import com.fatih.prayertime.domain.repository.LocalDataRepository
import javax.inject.Inject

class GetEsmaulHusnaUseCase @Inject constructor(private val localDataRepository : LocalDataRepository){

    suspend operator fun invoke() = localDataRepository.getEsmaulHusna()
}