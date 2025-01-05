package com.fatih.namazvakitleri.domain.repository

import com.fatih.namazvakitleri.util.Resource
import com.fatih.namazvakitleri.data.remote.dto.DailyPrayResponseDTO

interface PrayRepository {

    suspend fun getPrayTimes(date : String,latitude : Double, longitude : Double) : Resource<DailyPrayResponseDTO>
}