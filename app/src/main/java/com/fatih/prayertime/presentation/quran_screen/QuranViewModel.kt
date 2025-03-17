package com.fatih.prayertime.presentation.quran_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fatih.prayertime.domain.use_case.quran_use_cases.GetAudioListUseCase
import com.fatih.prayertime.domain.use_case.quran_use_cases.GetJuzListUseCase
import com.fatih.prayertime.domain.use_case.quran_use_cases.GetSelectedSurahUseCase
import com.fatih.prayertime.domain.use_case.quran_use_cases.GetSurahListUseCase
import com.fatih.prayertime.domain.use_case.quran_use_cases.GetTranslationListUseCase
import com.fatih.prayertime.domain.use_case.quran_use_cases.PlayAudioUseCase
import com.fatih.prayertime.data.audio.QuranAudioPlayer
import com.fatih.prayertime.util.extensions.toText
import com.fatih.prayertime.util.model.state.QuranScreenState
import com.fatih.prayertime.util.model.state.Status
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class QuranViewModel @Inject constructor(
    private val getSurahListUseCase: GetSurahListUseCase,
    private val getJuzListUseCase: GetJuzListUseCase,
    private val getTranslationListUseCase: GetTranslationListUseCase,
    private val getAudioListUseCase: GetAudioListUseCase,
    private val getSelectedSurahUseCase: GetSelectedSurahUseCase,
    private val playAudioUseCase: PlayAudioUseCase,
    private val audioPlayer: QuranAudioPlayer
) : ViewModel() {

    private val _state = MutableStateFlow(QuranScreenState())
    val state: StateFlow<QuranScreenState> = _state.asStateFlow()

    init {
        loadSurahList()
        loadJuzList()
        loadTranslationList()
        loadAudioList()
        setupAudioPlayerCallbacks()
    }

    fun getSelectedSurah(surahNumber: Int, onFinish: () -> Unit) =
        viewModelScope.launch(Dispatchers.IO) {
            _state.value = _state.value.copy(isLoading = true, selectedSurahNumber = surahNumber)
            val audioPathIdentifier =
                _state.value.reciterList.firstOrNull { it.toText() == _state.value.selectedReciter }?.identifier
                    ?: _state.value.reciterList[0].identifier
            val transliterationPath =
                _state.value.transliterationList[_state.value.selectedTransliteration]
            val surahResponse =
                getSelectedSurahUseCase(surahNumber, "$audioPathIdentifier,$transliterationPath")
            when (surahResponse.status) {
                Status.SUCCESS -> {
                    _state.value = _state.value.copy(
                        selectedSurah = surahResponse.data!!,
                        error = null,
                        isLoading = false,
                        currentAyahNumber = 1
                    )
                    if (_state.value.isAudioPlaying){
                        updateCurrentAyahNumber(0)
                    }
                }

                Status.ERROR -> {
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
            withContext(Dispatchers.Main) {
                onFinish()
            }
        }

    fun loadAudioList() = viewModelScope.launch(Dispatchers.IO) {
        val audioResponse = getAudioListUseCase()
        _state.value = _state.value.copy(isLoading = true)
        when (audioResponse.status) {
            Status.SUCCESS -> {
                _state.value = _state.value.copy(
                    reciterList = audioResponse.data!!,
                    selectedReciter = audioResponse.data[0].toText(),
                    error = null,
                    isLoading = false
                )
            }

            Status.ERROR -> {
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

    fun loadTranslationList() = viewModelScope.launch(Dispatchers.IO) {
        val translationResponse = getTranslationListUseCase()
        _state.value = _state.value.copy(isLoading = true)
        when (translationResponse.status) {
            Status.SUCCESS -> {
                _state.value = _state.value.copy(
                    translationList = translationResponse.data!!,
                    selectedTranslation = translationResponse.data[0].toText(),
                    error = null,
                    isLoading = false
                )
            }

            Status.ERROR -> {
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

    fun loadSurahList() = viewModelScope.launch(Dispatchers.IO) {
        val surahResponse = getSurahListUseCase()
        _state.emit(_state.value.copy(isLoading = true))
        when (surahResponse.status) {
            Status.SUCCESS -> {
                _state.value = _state.value.copy(
                    surahList = surahResponse.data!!,
                    error = null,
                    isLoading = false
                )
            }

            Status.ERROR -> {
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

    fun loadJuzList() = viewModelScope.launch(Dispatchers.IO) {
        val juzResponse = getJuzListUseCase()
        _state.value = _state.value.copy(isLoading = true)
        when (juzResponse.status) {
            Status.SUCCESS -> {
                _state.value = _state.value.copy(
                    juzList = juzResponse.data!!,
                    error = null,
                    isLoading = false
                )
            }

            Status.ERROR -> {
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

    fun onTransliterationSelected(transliteration: String) {
        _state.value = _state.value.copy(selectedTransliteration = transliteration)
    }

    fun updateCurrentAyahNumber(direction: Int)  {
        val selectedSurah = _state.value.selectedSurah ?: return
        val ayahSize = selectedSurah.ayahs?.size ?: return
        if (_state.value.currentAyahNumber == ayahSize) return
        _state.value = _state.value.copy(
            currentAyahNumber = (_state.value.currentAyahNumber + direction).coerceIn(
                1,
                ayahSize
            )
        )
        playAyahAudio()
    }

    // Ses oynatma işlevleri
    fun playAyahAudio() {
        val selectedSurah = _state.value.selectedSurah ?: return
        val ayah = selectedSurah.ayahs?.get(_state.value.currentAyahNumber - 1) ?: return
        val audioUrl = ayah.audio
        viewModelScope.launch(Dispatchers.IO) {
            _state.value = _state.value.copy(
                isAudioLoading = true
            )
            
            try {
                playAudioUseCase(audioUrl).collectLatest { resource ->
                    when (resource.status) {
                        Status.LOADING -> {
                            _state.value = _state.value.copy(isAudioLoading = true)
                        }
                        Status.SUCCESS -> {
                            resource.data?.let { file ->
                                audioPlayer.playAudio(file)
                                _state.value = _state.value.copy(
                                    isAudioPlaying = true,
                                    isAudioLoading = false
                                )
                            }
                        }
                        Status.ERROR -> {
                            _state.value = _state.value.copy(
                                isAudioLoading = false,
                                audioError = resource.message
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isAudioLoading = false,
                    audioError = e.message ?: "Ses dosyası oynatılamadı"
                )
            }
        }
    }

    fun pauseAudio() {
        audioPlayer.pauseAudio()
    }

    fun resumeAudio() {
        audioPlayer.resumeAudio{
            playAyahAudio()
        }
    }

    fun stopAudio() {
        audioPlayer.stopAudio()
    }

    fun seekTo(position: Float) {
        _state.value = _state.value.copy(
            currentAudioPosition = position
        )
        audioPlayer.seekTo(position)
    }

    private fun setupAudioPlayerCallbacks() {
        audioPlayer.setProgressCallback { position, duration ->
            _state.value = _state.value.copy(
                currentAudioPosition = position,
                audioDuration = duration
            )
        }
        
        audioPlayer.setCompletionCallback {
            updateCurrentAyahNumber(1)
        }
        
        audioPlayer.setErrorCallback { errorMessage ->
            _state.value = _state.value.copy(
                audioError = errorMessage
            )
        }
        audioPlayer.setIsPlayingCallback {
            _state.value = _state.value.copy(isAudioPlaying = it)
        }
    }


    override fun onCleared() {
        super.onCleared()
        audioPlayer.releaseMediaPlayer()
    }
}

