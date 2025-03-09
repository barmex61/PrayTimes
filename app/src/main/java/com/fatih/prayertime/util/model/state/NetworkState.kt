package com.fatih.prayertime.util.model.state

sealed class NetworkState {
    data object Connected : NetworkState()
    data object Disconnected : NetworkState()
} 