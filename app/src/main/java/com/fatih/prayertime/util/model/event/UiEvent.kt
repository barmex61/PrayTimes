package com.fatih.prayertime.util.model.event

sealed class UiEvent {
    data class Navigate(val route: String) : UiEvent()
    data class ShowToast(val message : String) : UiEvent()
}