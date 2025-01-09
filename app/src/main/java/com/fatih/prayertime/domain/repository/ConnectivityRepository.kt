package com.fatih.prayertime.domain.repository

import com.fatih.prayertime.util.NetworkState
import kotlinx.coroutines.flow.Flow

interface ConnectivityRepository {

    fun getConnectivityStatus() : Flow<NetworkState>
}