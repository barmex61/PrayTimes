package com.fatih.prayertime.domain.use_case.pray_times_use_cases

import com.fatih.prayertime.domain.model.PrayTimes
import com.fatih.prayertime.domain.repository.PrayDatabaseRepository
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
import javax.inject.Inject

class InsertPrayTimeIntoDbUseCase @Inject constructor(private val prayDatabaseRepository: PrayDatabaseRepository) {
    private val today = LocalDate.now()
    private val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
    suspend fun insertPrayTimeList(prayTimesList: List<PrayTimes>) {
        val filteredList = prayTimesList.filter {
            LocalDate.parse(it.date,formatter).isAfter(today) || LocalDate.parse(it.date,formatter).isEqual(today)
        }
        prayDatabaseRepository.insertAllPrayTimes(filteredList)
    }
}