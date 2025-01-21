package com.fatih.prayertime.domain.use_case.pray_times_use_cases

import com.fatih.prayertime.domain.model.Address
import com.fatih.prayertime.domain.model.PrayTimes
import com.fatih.prayertime.domain.repository.PrayApiRepository
import com.fatih.prayertime.util.Resource
import com.fatih.prayertime.util.toPrayTimes
import javax.inject.Inject

class GetMonthlyPrayTimesFromApiUseCase @Inject constructor(private val repository: PrayApiRepository)  {

    suspend operator fun invoke(year : Int, month : Int,address: Address) : Resource<List<PrayTimes>> {
        val response = repository.getMonthlyPrayTimes(year, month, address.latitude, address.longitude)
        return try {
            if (response.data != null) Resource.success(response.data.toPrayTimes(address))
            else Resource.error(response.message)
        }catch (e:Exception){
            Resource.error(e.localizedMessage)
        }
    }
}