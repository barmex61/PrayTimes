package com.fatih.prayertime.util

sealed class NetworkState {
    data object Connected : NetworkState()
    data object Disconnected : NetworkState()
}
