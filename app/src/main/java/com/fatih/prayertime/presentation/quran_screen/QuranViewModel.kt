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
import com.fatih.prayertime.util.model.state.AudioPlayerState
import com.fatih.prayertime.util.model.state.QuranScreenState
import com.fatih.prayertime.util.model.state.Status
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
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

    private val _quranScreenState = MutableStateFlow(QuranScreenState())
    val quranScreenState = _quranScreenState

    private val _audioPlayerState = MutableStateFlow(AudioPlayerState())
    val audioPlayerState = _audioPlayerState

    init {
        loadSurahList()
        loadJuzList()
        loadTranslationList()
        loadAudioList()
        setupAudioPlayerCallbacks()
    }

    fun getSelectedSurah(surahNumber: Int, onFinish: () -> Unit) =
        viewModelScope.launch(Dispatchers.IO) {
            _quranScreenState.update { it.copy(isLoading = true) }
            val recitePathIdentifier = quranScreenState.value.reciterList.firstOrNull { it.toText() == quranScreenState.value.selectedReciter }?.identifier ?: return@launch
            println(quranScreenState.value.selectedReciter)
            println(quranScreenState.value.reciterList.first().toText())
            val transliterationPath =
                _quranScreenState.value.transliterationList[_quranScreenState.value.selectedTransliteration]
            val surahResponse =
                getSelectedSurahUseCase(surahNumber, "$recitePathIdentifier,$transliterationPath")
            when (surahResponse.status) {
                Status.SUCCESS -> {
                    _quranScreenState.value = _quranScreenState.value.copy(
                        selectedSurah = surahResponse.data!!,
                        error = null,
                        isLoading = false,
                        currentAyahNumber = 1
                    )
                    if (_audioPlayerState.value.audioPlaying){
                        updateCurrentAyahNumber(0)
                    }
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
            withContext(Dispatchers.Main) {
                onFinish()
            }
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

    fun updateCurrentAyahNumber(direction: Int)  {
        val selectedSurah = _quranScreenState.value.selectedSurah ?: return
        val ayahSize = selectedSurah.ayahs?.size ?: return
        if (_quranScreenState.value.currentAyahNumber == ayahSize && direction == 1) return
        _quranScreenState.value = _quranScreenState.value.copy(
            currentAyahNumber = (_quranScreenState.value.currentAyahNumber + direction).coerceIn(
                1,
                ayahSize
            )
        )
        playAyahAudio()
    }

    fun playAyahAudio() {
        val selectedSurah = _quranScreenState.value.selectedSurah ?: return
        val ayah = selectedSurah.ayahs?.get(_quranScreenState.value.currentAyahNumber - 1) ?: return
        val audioUrl = ayah.audio
        println(audioUrl)
        viewModelScope.launch(Dispatchers.IO) {
            _audioPlayerState.update { it.copy(audioLoading = true) }
            
            try {
                playAudioUseCase(audioUrl).collectLatest { resource ->
                    when (resource.status) {
                        Status.LOADING -> {
                            _audioPlayerState.value = _audioPlayerState.value.copy(audioLoading = true)
                        }
                        Status.SUCCESS -> {
                            resource.data?.let { file ->
                                audioPlayer.playAudio(file)
                                _audioPlayerState.value = _audioPlayerState.value.copy(
                                    audioPlaying = true,
                                    audioLoading = false
                                )
                            }
                        }
                        Status.ERROR -> {
                            _audioPlayerState.value = _audioPlayerState.value.copy(
                                audioLoading = false,
                                audioError = resource.message
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                _audioPlayerState.value = _audioPlayerState.value.copy(
                    audioLoading = false,
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
        _audioPlayerState.update { it.copy(currentAudioPosition = position) }
        audioPlayer.seekTo(position)
    }

    private fun setupAudioPlayerCallbacks() {
        audioPlayer.setProgressCallback { position, duration ->
            _audioPlayerState.value = _audioPlayerState.value.copy(currentAudioPosition = position, audioDuration = duration)
        }
        
        audioPlayer.setCompletionCallback {
            updateCurrentAyahNumber(1)
        }
        
        audioPlayer.setErrorCallback { errorMessage ->
            _audioPlayerState.update { it.copy(audioError = errorMessage) }
        }
        audioPlayer.setIsPlayingCallback { isPlaying ->
            _audioPlayerState.update { it.copy(audioPlaying = isPlaying) }
        }
    }


    override fun onCleared() {
        super.onCleared()
        audioPlayer.releaseMediaPlayer()
    }
}

