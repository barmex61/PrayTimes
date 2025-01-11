package com.fatih.prayertime.domain.use_case.pray_times_use_cases.get_daily_pray_times_use_case

import com.fatih.prayertime.domain.model.Address
import com.fatih.prayertime.domain.model.PrayTimes
import com.fatih.prayertime.domain.repository.PrayApiRepository
import com.fatih.prayertime.util.Resource
import com.fatih.prayertime.util.toPrayTimes
import javax.inject.Inject

class GetDailyPrayTimesFromApiUseCase @Inject constructor(private val repository: PrayApiRepository)  {

    suspend operator fun invoke(date : String,address: Address) : Resource<PrayTimes> {
        val response = repository.getPrayTimes(date, address.latitude, address.longitude)
        return try {
            if (response.data != null) Resource.success(response.data.toPrayTimes(address))
            else Resource.error(response.message)
        }catch (e:Exception){
            Resource.error(e.localizedMessage)
        }
    }
}