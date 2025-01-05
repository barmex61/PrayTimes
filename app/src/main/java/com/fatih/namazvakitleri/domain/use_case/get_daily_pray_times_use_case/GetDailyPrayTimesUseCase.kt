package com.fatih.namazvakitleri.domain.use_case.get_daily_pray_times_use_case

import com.fatih.namazvakitleri.domain.model.DailyPrayResponse
import com.fatih.namazvakitleri.domain.repository.PrayRepository
import com.fatih.namazvakitleri.util.Resource
import com.fatih.namazvakitleri.util.toDailyPrayResponse
import javax.inject.Inject

class GetDailyPrayTimesUseCase @Inject constructor(private val repository: PrayRepository)  {

    suspend operator fun invoke(date : String,latitude : Double, longitude : Double) : Resource<DailyPrayResponse> {
        val response = repository.getPrayTimes(date, latitude, longitude)
        return try {
            if (response.data != null) Resource.success(response.data.toDailyPrayResponse())
            else Resource.error(response.message)
        }catch (e:Exception){
            Resource.error(e.localizedMessage)
        }
    }
}