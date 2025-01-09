package com.fatih.prayertime.domain.use_case.get_network_state_use_case

import com.fatih.prayertime.domain.repository.ConnectivityRepository
import javax.inject.Inject

class GetNetworkStateUseCase @Inject constructor(private val connectivityRepository: ConnectivityRepository) {

    suspend operator fun invoke() = connectivityRepository.getConnectivityStatus()
}