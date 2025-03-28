package com.fatih.prayertime.domain.use_case.pray_times_use_cases

import com.fatih.prayertime.domain.model.Address
import com.fatih.prayertime.domain.model.PrayTimes
import com.fatih.prayertime.domain.repository.PrayApiRepository
import com.fatih.prayertime.util.extensions.toPrayTimes
import com.fatih.prayertime.util.model.state.Resource
import javax.inject.Inject

class GetMonthlyPrayTimesFromApiUseCase @Inject constructor(private val repository: PrayApiRepository)  {

    suspend operator fun invoke(
        year: Int, 
        month: Int, 
        address: Address,
        method: Int = 13,
        adjustments: String? = null,
        tuneString: String? = null,
        school: Int = 0,
        midnightMode: Int = 0
    ): Resource<List<PrayTimes>> {
        return try {
            val response = repository.getMonthlyPrayTimes(
                year, 
                month, 
                address.latitude, 
                address.longitude,
                method,
                adjustments,
                tuneString,
                school,
                midnightMode
            )
            if (response.data != null) Resource.success(response.data.toPrayTimes(address))
            else Resource.error(response.message)
        } catch (e: Exception) {
            Resource.error(e.localizedMessage)
        }
    }
}