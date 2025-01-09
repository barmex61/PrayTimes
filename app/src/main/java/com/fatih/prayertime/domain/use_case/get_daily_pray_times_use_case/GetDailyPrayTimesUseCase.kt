package com.fatih.prayertime.domain.use_case.get_daily_pray_times_use_case

import com.fatih.prayertime.domain.model.PrayTimes
import com.fatih.prayertime.domain.repository.PrayRepository
import com.fatih.prayertime.util.Resource
import com.fatih.prayertime.util.toPrayTimes
import javax.inject.Inject

class GetDailyPrayTimesUseCase @Inject constructor(private val repository: PrayRepository)  {

    suspend operator fun invoke(date : String,latitude : Double, longitude : Double) : Resource<PrayTimes> {
        val response = repository.getPrayTimes(date, latitude, longitude)
        return try {
            if (response.data != null) Resource.success(response.data.toPrayTimes())
            else Resource.error(response.message)
        }catch (e:Exception){
            Resource.error(e.localizedMessage)
        }
    }
}