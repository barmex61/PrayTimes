package com.fatih.prayertime.data.repository

import com.fatih.prayertime.data.network.NetworkConnectivityManager
import com.fatih.prayertime.domain.repository.ConnectivityRepository
import com.fatih.prayertime.util.model.state.NetworkState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class ConnectivityRepositoryImp @Inject constructor(private val networkConnectivityManager: NetworkConnectivityManager) : ConnectivityRepository {

    override suspend fun getConnectivityStatus(): Flow<NetworkState>  = callbackFlow{
        networkConnectivityManager.observe({
            trySend(NetworkState.Connected)
        }){
            trySend(NetworkState.Disconnected)
        }
        awaitClose {
            networkConnectivityManager.unObserve()
        }
    }.flowOn(Dispatchers.IO)

}