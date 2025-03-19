package com.fatih.prayertime.presentation.quran_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fatih.prayertime.domain.use_case.quran_use_cases.GetAudioListUseCase
import com.fatih.prayertime.domain.use_case.quran_use_cases.GetJuzListUseCase
import com.fatih.prayertime.domain.use_case.quran_use_cases.GetSurahListUseCase
import com.fatih.prayertime.domain.use_case.quran_use_cases.GetTranslationListUseCase

import com.fatih.prayertime.util.extensions.toText

import com.fatih.prayertime.util.model.state.QuranScreenState
import com.fatih.prayertime.util.model.state.Status
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuranViewModel @Inject constructor(
    private val getSurahListUseCase: GetSurahListUseCase,
    private val getJuzListUseCase: GetJuzListUseCase,
    private val getTranslationListUseCase: GetTranslationListUseCase,
    private val getAudioListUseCase: GetAudioListUseCase,
) : ViewModel() {

    private val _quranScreenState = MutableStateFlow(QuranScreenState())
    val quranScreenState = _quranScreenState

    init {
        loadSurahList()
        loadJuzList()
        loadTranslationList()
        loadAudioList()
        println("initquran")
    }


    fun loadAudioList() = viewModelScope.launch(Dispatchers.IO) {
        val audioResponse = getAudioListUseCase()
        _quranScreenState.value = _quranScreenState.value.copy(isLoading = true)
        when (audioResponse.status) {
            Status.SUCCESS -> {
                _quranScreenState.value = _quranScreenState.value.copy(
                    reciterList = audioResponse.data!!,
                    selectedReciter = audioResponse.data[0].toText(),
                    error = null,
                    isLoading = false
                )
            }

            Status.ERROR -> {
                _quranScreenState.value = _quranScreenState.value.copy(
                    error = audioResponse.message!!,
                    isLoading = false
                )
            }

            else -> {
                _quranScreenState.value = _quranScreenState.value.copy(
                    error = null,
                    isLoading = true
                )
            }
        }
    }

    fun loadTranslationList() = viewModelScope.launch(Dispatchers.IO) {
        val translationResponse = getTranslationListUseCase()
        _quranScreenState.value = _quranScreenState.value.copy(isLoading = true)
        when (translationResponse.status) {
            Status.SUCCESS -> {
                _quranScreenState.value = _quranScreenState.value.copy(
                    translationList = translationResponse.data!!,
                    selectedTranslation = translationResponse.data[0].toText(),
                    error = null,
                    isLoading = false
                )
            }

            Status.ERROR -> {
                _quranScreenState.value = _quranScreenState.value.copy(
                    error = translationResponse.message!!,
                    isLoading = false
                )
            }

            else -> {
                _quranScreenState.value = _quranScreenState.value.copy(
                    error = null,
                    isLoading = true
                )
            }
        }
    }

    fun loadSurahList() = viewModelScope.launch(Dispatchers.IO) {
        val surahResponse = getSurahListUseCase()
        _quranScreenState.emit(_quranScreenState.value.copy(isLoading = true))
        when (surahResponse.status) {
            Status.SUCCESS -> {
                _quranScreenState.value = _quranScreenState.value.copy(
                    surahList = surahResponse.data!!,
                    error = null,
                    isLoading = false
                )
            }

            Status.ERROR -> {
                _quranScreenState.value = _quranScreenState.value.copy(
                    error = surahResponse.message!!,
                    isLoading = false
                )
            }

            else -> {
                _quranScreenState.value = _quranScreenState.value.copy(
                    error = null,
                    isLoading = true
                )
            }
        }
    }

    fun loadJuzList() = viewModelScope.launch(Dispatchers.IO) {
        val juzResponse = getJuzListUseCase()
        _quranScreenState.value = _quranScreenState.value.copy(isLoading = true)
        when (juzResponse.status) {
            Status.SUCCESS -> {
                _quranScreenState.value = _quranScreenState.value.copy(
                    juzList = juzResponse.data!!,
                    error = null,
                    isLoading = false
                )
            }

            Status.ERROR -> {
                _quranScreenState.value = _quranScreenState.value.copy(
                    error = juzResponse.message!!,
                    isLoading = false
                )
            }

            else -> {
                _quranScreenState.value = _quranScreenState.value.copy(
                    error = null,
                    isLoading = true
                )
            }
        }
    }

    fun onTabSelected(index: Int) {
        _quranScreenState.value = _quranScreenState.value.copy(selectedTabIndex = index)
    }

    fun onReciterSelected(reciter: String) {
        _quranScreenState.value = _quranScreenState.value.copy(selectedReciter = reciter)
    }

    fun onTranslationSelected(translation: String) {
        _quranScreenState.value = _quranScreenState.value.copy(selectedTranslation = translation)
    }

    fun onTransliterationSelected(transliteration: String) {
        _quranScreenState.value = _quranScreenState.value.copy(selectedTransliteration = transliteration)
    }

}

