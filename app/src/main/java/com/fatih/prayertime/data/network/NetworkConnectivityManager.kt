package com.fatih.prayertime.data.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.core.content.ContextCompat

class NetworkConnectivityManager(context: Context) {

    private val connectivityManager = ContextCompat.getSystemService(context, ConnectivityManager::class.java)
    private var networkCallback: ConnectivityManager.NetworkCallback? = null

    fun observe(onNetworkAvailable: () -> Unit, onNetworkUnavailable: () -> Unit) {
        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()
        if (networkCallback == null){
            networkCallback = object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    println("onAvaible")
                    onNetworkAvailable()
                }

                override fun onLost(network: Network) {
                    println("onLost")
                    onNetworkUnavailable()
                }
            }
        }
        connectivityManager?.registerNetworkCallback(request, networkCallback!!)
    }

    fun unObserve() {
        if(networkCallback != null && connectivityManager != null) {
            connectivityManager.unregisterNetworkCallback(networkCallback!!)
            networkCallback = null
        }
    }
}