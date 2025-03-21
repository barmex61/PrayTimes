package com.fatih.prayertime.util.model.state

import com.fatih.prayertime.data.remote.dto.qurandto.SurahInfo

data class QuranDetailScreenState(
    val isLoading : Boolean = false,
    val isError : String? = null,
    val selectedSurah : SurahInfo? = null,
    val selectedSurahNumber : Int = 0,
    val selectedAyahNumber : Int = 1,

)
