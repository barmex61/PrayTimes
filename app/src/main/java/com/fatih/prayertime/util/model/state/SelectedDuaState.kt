package com.fatih.prayertime.util.model.state

import com.fatih.prayertime.data.remote.dto.duadto.DuaCategoryDetail

data class SelectedDuaState(
    val dua : DuaCategoryDetail?= null,
    val isVisible: Boolean = false
)