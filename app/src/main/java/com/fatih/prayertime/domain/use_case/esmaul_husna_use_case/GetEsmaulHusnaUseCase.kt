package com.fatih.prayertime.domain.use_case.esmaul_husna_use_case

import com.fatih.prayertime.domain.repository.LocalDataRepository
import javax.inject.Inject

class GetEsmaulHusnaUseCase @Inject constructor(private val localDataRepository : LocalDataRepository){

    operator fun invoke() = localDataRepository.getEsmaulHusna()
}