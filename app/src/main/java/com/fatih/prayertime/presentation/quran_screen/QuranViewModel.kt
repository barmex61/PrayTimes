package com.fatih.prayertime.presentation.quran_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fatih.prayertime.domain.use_case.quran_use_cases.GetJuzListUseCase
import com.fatih.prayertime.domain.use_case.quran_use_cases.GetSurahListUseCase
import com.fatih.prayertime.util.model.state.QuranScreenState
import com.fatih.prayertime.util.model.state.Status
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuranViewModel @Inject constructor(
    private val getSurahListUseCase: GetSurahListUseCase,
    private val getJuzListUseCase: GetJuzListUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(QuranScreenState())
    val state: StateFlow<QuranScreenState> = _state.asStateFlow()

    init {
        loadSurahList()
        loadJuzList()
    }

    private fun loadSurahList()  = viewModelScope.launch(Dispatchers.IO){
        val surahResponse = getSurahListUseCase()
        when(surahResponse.status){
            Status.SUCCESS->{
                _state.value = _state.value.copy(
                    surahList = surahResponse.data!!,
                    error = null,
                    isLoading = false
                )
            }
            Status.ERROR->{
                _state.value = _state.value.copy(
                    error = surahResponse.message!!,
                    isLoading = false
                )
            }
            else -> {
                _state.value = _state.value.copy(
                    error = null,
                    isLoading = true
                )
            }
        }
    }

    private fun loadJuzList() = viewModelScope.launch(Dispatchers.IO){
       val juzResponse = getJuzListUseCase()
        when(juzResponse.status){
            Status.SUCCESS->{
                _state.value = _state.value.copy(
                    juzList = juzResponse.data!!,
                    error = null,
                    isLoading = false
                )
            }
            Status.ERROR->{
                _state.value = _state.value.copy(
                    error = juzResponse.message!!,
                    isLoading = false
                )
            }
            else -> {
                _state.value = _state.value.copy(
                    error = null,
                    isLoading = true
                )
            }
        }
    }

    fun onTabSelected(index: Int) {
        _state.value = _state.value.copy(selectedTabIndex = index)
    }

    fun onReciterSelected(reciter: String) {
        _state.value = _state.value.copy(selectedReciter = reciter)
    }

    fun onTranslationSelected(translation: String) {
        _state.value = _state.value.copy(selectedTranslation = translation)
    }

    fun onPronunciationSelected(pronunciation: String) {
        _state.value = _state.value.copy(selectedPronunciation = pronunciation)
    }
} 