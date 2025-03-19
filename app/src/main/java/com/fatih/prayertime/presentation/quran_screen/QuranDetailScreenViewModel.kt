package com.fatih.prayertime.presentation.quran_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fatih.prayertime.data.audio.QuranAudioManager
import com.fatih.prayertime.domain.use_case.quran_use_cases.GetAudioListUseCase
import com.fatih.prayertime.domain.use_case.quran_use_cases.GetSelectedSurahUseCase
import com.fatih.prayertime.domain.use_case.quran_use_cases.GetTranslationListUseCase
import com.fatih.prayertime.domain.use_case.quran_use_cases.GetAudioFileUseCase
import com.fatih.prayertime.domain.use_case.settings_use_cases.GetSettingsUseCase
import com.fatih.prayertime.domain.use_case.settings_use_cases.SaveSettingsUseCase
import com.fatih.prayertime.util.extensions.toText
import com.fatih.prayertime.util.model.event.QuranDetailScreenEvent
import com.fatih.prayertime.util.model.state.AudioPlayerState
import com.fatih.prayertime.util.model.state.QuranDetailScreenState
import com.fatih.prayertime.util.model.state.QuranSettingsState
import com.fatih.prayertime.util.model.state.Status
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuranDetailScreenViewModel @Inject constructor(
    private val getSelectedSurahUseCase: GetSelectedSurahUseCase,
    private val quranAudioManager: QuranAudioManager,
    private val getSettingsUseCase: GetSettingsUseCase,
    private val getAudioFileUseCase: GetAudioFileUseCase,
    private val saveSettingsUseCase: SaveSettingsUseCase,
    private val getTranslationListUseCase: GetTranslationListUseCase,
    private val getAudioListUseCase: GetAudioListUseCase
    ) : ViewModel() {

    fun loadAudioList() = viewModelScope.launch(Dispatchers.IO) {
        val audioResponse = getAudioListUseCase()
        _quranSettingsState.value = _quranSettingsState.value.copy(isLoading = true)
        when (audioResponse.status) {
            Status.SUCCESS -> {
                _quranSettingsState.value = _quranSettingsState.value.copy(
                    reciterList = audioResponse.data!!,
                    selectedReciter = audioResponse.data[0].toText(),
                    selectedReciterIndex = 0,
                    isError = null,
                    isLoading = false
                )
            }

            Status.ERROR -> {
                _quranSettingsState.value = _quranSettingsState.value.copy(
                    isError = audioResponse.message!!,
                    isLoading = false
                )
            }

            else -> {
                _quranSettingsState.value = _quranSettingsState.value.copy(
                    isError = null,
                    isLoading = true
                )
            }
        }
    }

    fun loadTranslationList() = viewModelScope.launch(Dispatchers.IO) {
        val translationResponse = getTranslationListUseCase()
        _quranSettingsState.value = _quranSettingsState.value.copy(isLoading = true)
        when (translationResponse.status) {
            Status.SUCCESS -> {
                _quranSettingsState.value = _quranSettingsState.value.copy(
                    translationList = translationResponse.data!!,
                    selectedTranslation = translationResponse.data[0].toText(),
                    isError = null,
                    isLoading = false
                )
            }

            Status.ERROR -> {
                _quranSettingsState.value = _quranSettingsState.value.copy(
                    isError = translationResponse.message!!,
                    isLoading = false
                )
            }

            else -> {
                _quranSettingsState.value = _quranSettingsState.value.copy(
                    isError = null,
                    isLoading = true
                )
            }
        }
    }

    private val _quranDetailScreenState = MutableStateFlow(QuranDetailScreenState())
    val quranDetailScreenState = _quranDetailScreenState

    private val _audioPlayerState = MutableStateFlow(AudioPlayerState())
    val audioPlayerState = _audioPlayerState

    private val _quranSettingsState = MutableStateFlow(QuranSettingsState())
    val quranSettingsState = _quranSettingsState

    suspend fun getSelectedSurah(surahNumber: Int) {
            _quranDetailScreenState.update { it.copy(isLoading = true) }
            val recitePathIdentifier = _quranSettingsState.value.reciterList[_quranSettingsState.value.selectedReciterIndex].identifier
            val transliterationPath = _quranSettingsState.value.transliterationList[_quranSettingsState.value.selectedTransliteration]
            val surahResponse = getSelectedSurahUseCase(surahNumber, "$recitePathIdentifier,$transliterationPath")
            when (surahResponse.status) {
                Status.SUCCESS -> {
                    _quranDetailScreenState.value = _quranDetailScreenState.value.copy(
                        selectedSurah = surahResponse.data!!,
                        isError = null,
                        isLoading = false,
                        selectedAyahNumber = _quranDetailScreenState.value.selectedAyahNumber
                    )
                    if (_audioPlayerState.value.audioPlaying) {
                        updateCurrentAyahNumber(0)
                    }else{
                        stopAudio()
                    }

                }

                Status.ERROR -> {
                    _quranDetailScreenState.value = _quranDetailScreenState.value.copy(
                        isError = surahResponse.message!!,
                        isLoading = false
                    )
                }

                else -> {
                    _quranDetailScreenState.value = _quranDetailScreenState.value.copy(
                        isError = null,
                        isLoading = true
                    )
                }
            }
        }


    fun updateCurrentAyahNumber(direction: Int)  {
        val selectedSurah = _quranDetailScreenState.value.selectedSurah ?: return
        val ayahSize = selectedSurah.ayahs?.size ?: return
        if (_quranDetailScreenState.value.selectedAyahNumber == ayahSize && direction == 1) {
            _audioPlayerState.update { it.copy(audioPlaying = false) }
            return
        }
        _quranDetailScreenState.value = _quranDetailScreenState.value.copy(
            selectedAyahNumber = (_quranDetailScreenState.value.selectedAyahNumber + direction).coerceIn(
                1,
                ayahSize
            )
        )
        playAyahAudio()
    }


    fun playAyahAudio() {
        val selectedSurah = _quranDetailScreenState.value.selectedSurah ?: return
        val ayah = selectedSurah.ayahs?.get(_quranDetailScreenState.value.selectedAyahNumber - 1) ?: return
        val shouldCacheAudio = _quranSettingsState.value.shouldCacheAudio
        val audioUrl = ayah.audio
        val reciteLink = _quranSettingsState.value.reciterList[_quranSettingsState.value.selectedReciterIndex].identifier
        val reciteName = _quranSettingsState.value.selectedReciter.substringAfter('-')
        quranAudioManager.setCurrentAudioInfo(selectedSurah.englishName,ayah.number,reciteLink,reciteName,quranSettingsState.value.shouldCacheAudio)
        viewModelScope.launch(Dispatchers.IO) {
            _audioPlayerState.update { it.copy(audioLoading = true) }

            try {
                getAudioFileUseCase(audioUrl,shouldCacheAudio).collectLatest { resource ->
                    when (resource.status) {
                        Status.LOADING -> {
                            _audioPlayerState.value = _audioPlayerState.value.copy(audioLoading = true)
                        }
                        Status.SUCCESS -> {
                            resource.data?.let { file ->
                                quranAudioManager.playAudio(file)
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
        quranAudioManager.pauseAudio()
    }

    fun resumeAudio() {
        quranAudioManager.resumeAudio{
            playAyahAudio()
        }
    }

    fun stopAudio() {
        quranAudioManager.stopAudio()
    }

    fun seekTo(position: Float) {
        _audioPlayerState.update { it.copy(currentAudioPosition = position) }
        quranAudioManager.seekTo(position)
    }

    private fun setupAudioPlayerCallbacks() {
        quranAudioManager.setProgressCallback { position, duration ->
            _audioPlayerState.value = _audioPlayerState.value.copy(currentAudioPosition = position, audioDuration = duration)
        }

        quranAudioManager.setCompletionCallback {
            updateCurrentAyahNumber(1)
        }
        quranAudioManager.setAyahChangedCallback { direction ->
            updateCurrentAyahNumber(direction)
        }

        quranAudioManager.setErrorCallback { errorMessage ->
            _audioPlayerState.update { it.copy(audioError = errorMessage) }
        }
        quranAudioManager.setIsPlayingCallback { isPlaying ->
            _audioPlayerState.update { it.copy(audioPlaying = isPlaying)}
        }

    }

    fun onSettingsEvent(event: QuranDetailScreenEvent) = viewModelScope.launch(Dispatchers.Default){
        when (event) {
            is QuranDetailScreenEvent.ToggleAutoHidePlayer -> {
                _quranSettingsState.update { it.copy(autoHidePlayer = !it.autoHidePlayer) }
            }
            is QuranDetailScreenEvent.SetPlaybackSpeed -> {
                _quranSettingsState.update { it.copy(playbackSpeed = event.speed) }
                quranAudioManager.setPlaybackSpeed(event.speed)
            }
            is QuranDetailScreenEvent.TogglePlaybackMode -> {
                _quranSettingsState.update { it.copy(playByVerse = !it.playByVerse) }
            }

            is QuranDetailScreenEvent.SetShouldCacheAudio -> {
                val settings = getSettingsUseCase.invoke().first()
                saveSettingsUseCase(settings.copy(shouldCacheAudio = event.shouldCache))
            }

            is QuranDetailScreenEvent.ToggleSettingsSheet -> {
                _quranSettingsState.update { it.copy(showSettings = !it.showSettings) }
            }

            is QuranDetailScreenEvent.ToggleCacheInfoDialog -> {
                _quranSettingsState.update { it.copy(showCacheInfo = !it.showCacheInfo) }
            }
            is QuranDetailScreenEvent.SetTranslation -> {
                _quranSettingsState.update { it.copy(selectedTranslation = event.translation) }
            }

            is QuranDetailScreenEvent.SetReciter -> {
                _quranSettingsState.update { it.copy(selectedReciter = event.reciter, selectedReciterIndex = event.reciterIndex) }
                stopAudio()
            }

            is QuranDetailScreenEvent.SetTransliteration -> {
                _quranSettingsState.update { it.copy(selectedTransliteration = event.transliteration) }
            }

            else ->{}
        }
    }

    init {
        setupAudioPlayerCallbacks()
        loadAudioList()
        loadTranslationList()

        viewModelScope.launch(Dispatchers.IO){
            getSettingsUseCase.invoke().collect {setting ->
                _quranSettingsState.update { it.copy(shouldCacheAudio = setting.shouldCacheAudio) }
            }
        }
        viewModelScope.launch(Dispatchers.IO) {
            _quranSettingsState
                .map { settings ->
                    Triple(
                        settings.selectedTranslation,
                        settings.selectedTransliteration,
                        settings.selectedReciter
                    )
                }
                .distinctUntilChanged()
                .collect { (translation, transliteration, reciter) ->

                    if (transliteration.isNotEmpty() && reciter.isNotEmpty()){
                        getSelectedSurah(quranDetailScreenState.value.selectedSurahNumber)
                    }
                }
        }
    }

    private fun releaseCallbacks() {
        quranAudioManager.setProgressCallback(null)
        quranAudioManager.setCompletionCallback(null)
        quranAudioManager.setAyahChangedCallback(null)
        quranAudioManager.setIsPlayingCallback(null)
        quranAudioManager.setErrorCallback(null)
    }

    override fun onCleared() {
        super.onCleared()
        releaseCallbacks()
        //quranAudioManager.stopAudio()
       // quranAudioManager.releaseMediaPlayer()
    }

}