package com.fatih.prayertime.presentation.quran_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fatih.prayertime.domain.use_case.quran_use_cases.GetJuzListUseCase
import com.fatih.prayertime.domain.use_case.quran_use_cases.GetSurahListUseCase
import com.fatih.prayertime.util.extensions.withRetry
import com.fatih.prayertime.util.model.state.QuranScreenState
import com.fatih.prayertime.util.model.state.Status
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuranViewModel @Inject constructor(
    getSurahListUseCase: GetSurahListUseCase,
    getJuzListUseCase: GetJuzListUseCase
) : ViewModel() {

    private val screenStateRetryTrigger = MutableSharedFlow<Unit>()
    private val selectedTab = MutableStateFlow(0)

    private val _quranScreenState = combine(
        getSurahListUseCase().withRetry(screenStateRetryTrigger),
        getJuzListUseCase().withRetry(screenStateRetryTrigger),
        selectedTab
    ) { surahResource, juzResource,selectedTab ->
        QuranScreenState(
            surahList = surahResource.data ?: emptyList(),
            juzList = juzResource.data ?: emptyList(),
            isLoading = surahResource.status == Status.LOADING || juzResource.status == Status.LOADING,
            error = when {
                surahResource.status == Status.ERROR -> surahResource.message
                juzResource.status == Status.ERROR -> juzResource.message
                else -> null
            },
            selectedTabIndex = selectedTab
        )
    }.distinctUntilChanged().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = QuranScreenState()
    )
    val quranScreenState = _quranScreenState

    fun retry() = viewModelScope.launch {
        screenStateRetryTrigger.emit(Unit)
    }

    fun onTabSelected(index: Int) {
        selectedTab.update { index }
    }
}

