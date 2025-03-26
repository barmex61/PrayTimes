package com.fatih.prayertime.presentation.hadith_screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fatih.prayertime.data.remote.dto.hadithdto.HadithEdition
import com.fatih.prayertime.domain.use_case.hadith_use_cases.GetHadithEditionsUseCase
import com.fatih.prayertime.util.extensions.withRetry
import com.fatih.prayertime.util.model.state.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HadithEditionsViewModel @Inject constructor(
    getHadithEditionsUseCase: GetHadithEditionsUseCase
): ViewModel() {

    private val retryTrigger = MutableSharedFlow<Unit>()

    val hadithEditions = getHadithEditionsUseCase().withRetry(retryTrigger)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Resource.loading())

    fun triggerRetry() = viewModelScope.launch {
        retryTrigger.emit(Unit)
    }
}