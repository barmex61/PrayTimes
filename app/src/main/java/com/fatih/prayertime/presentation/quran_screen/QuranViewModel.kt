package com.fatih.prayertime.presentation.quran_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fatih.prayertime.domain.use_case.quran_use_cases.GetAudioListUseCase
import com.fatih.prayertime.domain.use_case.quran_use_cases.GetJuzListUseCase
import com.fatih.prayertime.domain.use_case.quran_use_cases.GetSelectedSurah
import com.fatih.prayertime.domain.use_case.quran_use_cases.GetSurahListUseCase
import com.fatih.prayertime.domain.use_case.quran_use_cases.GetTranslationListUseCase
import com.fatih.prayertime.util.extensions.toText
import com.fatih.prayertime.util.model.state.QuranScreenState
import com.fatih.prayertime.util.model.state.Status
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class QuranViewModel @Inject constructor(
    private val getSurahListUseCase: GetSurahListUseCase,
    private val getJuzListUseCase: GetJuzListUseCase,
    private val getTranslationListUseCase: GetTranslationListUseCase,
    private val getAudioListUseCase: GetAudioListUseCase,
    private val getSelectedSurahUseCase: GetSelectedSurah
) : ViewModel() {

    private val _state = MutableStateFlow(QuranScreenState())
    val state: StateFlow<QuranScreenState> = _state.asStateFlow()

    init {
        loadSurahList()
        loadJuzList()
        loadTranslationList()
        loadAudioList()
    }

    fun getSelectedSurah(surahNumber : Int,onFinish : () -> Unit) = viewModelScope.launch(Dispatchers.IO){
        val surahResponse = getSelectedSurahUseCase(surahNumber)
        _state.value = _state.value.copy(isLoading = true, selectedSurahNumber = surahNumber)
        when(surahResponse.status){
            Status.SUCCESS->{
                _state.value = _state.value.copy(
                    selectedSurah = surahResponse.data!!,
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
        withContext(Dispatchers.Main){
            onFinish()
        }
    }

    fun loadAudioList() = viewModelScope.launch(Dispatchers.IO){
        val audioResponse = getAudioListUseCase()
        _state.value = _state.value.copy(isLoading = true)
        when(audioResponse.status){
            Status.SUCCESS->{
                _state.value = _state.value.copy(
                    reciterList = audioResponse.data!!,
                    selectedReciter = audioResponse.data[0].toText(),
                    error = null,
                    isLoading = false
                )
            }
            Status.ERROR->{
                _state.value = _state.value.copy(
                    error = audioResponse.message!!,
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

    fun loadTranslationList() = viewModelScope.launch(Dispatchers.IO){
        val translationResponse = getTranslationListUseCase()
        _state.value = _state.value.copy(isLoading = true)
        when(translationResponse.status){
            Status.SUCCESS->{
                _state.value = _state.value.copy(
                    translationList = translationResponse.data!!,
                    selectedTranslation = translationResponse.data[0].toText(),
                    error = null,
                    isLoading = false
                )
            }
            Status.ERROR->{
                _state.value = _state.value.copy(
                    error = translationResponse.message!!,
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

    fun loadSurahList()  = viewModelScope.launch(Dispatchers.IO){
        val surahResponse = getSurahListUseCase()
        _state.emit(_state.value.copy(isLoading = true))
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

    fun loadJuzList() = viewModelScope.launch(Dispatchers.IO){
       val juzResponse = getJuzListUseCase()
        _state.value = _state.value.copy(isLoading = true)
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

    init {
        viewModelScope.launch(Dispatchers.IO){
            state.collect {
                println("isLoading: ${it.isLoading}")
                println("error: ${it.error}")
            }
        }

    }
} 