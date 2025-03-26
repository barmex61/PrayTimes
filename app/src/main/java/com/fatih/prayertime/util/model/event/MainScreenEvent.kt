package com.fatih.prayertime.util.model.event

import com.fatih.prayertime.data.remote.dto.duadto.DuaCategoryDetail

sealed class MainScreenEvent {
    object ShowDuaDialog : MainScreenEvent()
    object HideDuaDialog : MainScreenEvent()
}