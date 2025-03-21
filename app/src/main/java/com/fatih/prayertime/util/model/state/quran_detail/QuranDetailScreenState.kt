package com.fatih.prayertime.util.model.state.quran_detail

import com.fatih.prayertime.data.remote.dto.qurandto.SurahInfo

data class QuranDetailScreenState(

    val selectedSurah : SurahInfo? = null,
    val selectedSurahNumber : Int = 0,
    val selectedAyahNumber : Int = 1,

    val isError: String? = null,
    val isLoading: Boolean = false

)
