package com.fatih.prayertime.presentation.quran_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fatih.prayertime.data.audio.QuranAudioManager
import com.fatih.prayertime.domain.use_case.quran_use_cases.GetAudioListUseCase
import com.fatih.prayertime.domain.use_case.quran_use_cases.GetSelectedSurahUseCase
import com.fatih.prayertime.domain.use_case.quran_use_cases.GetTranslationListUseCase
import com.fatih.prayertime.domain.use_case.quran_use_cases.GetAudioFileUseCase
import com.fatih.prayertime.domain.use_case.settings_use_cases.GetAudioSettingsUseCase
import com.fatih.prayertime.domain.use_case.settings_use_cases.SaveAudioSettingsUseCase
import com.fatih.prayertime.util.extensions.toText
import com.fatih.prayertime.util.model.event.QuranDetailScreenEvent
import com.fatih.prayertime.util.model.state.AudioPlayerState
import com.fatih.prayertime.util.model.state.QuranDetailScreenState
import com.fatih.prayertime.util.model.state.QuranSettingsState
import com.fatih.prayertime.util.model.state.Status
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class QuranDetailScreenViewModel @Inject constructor(
    private val getSelectedSurahUseCase: GetSelectedSurahUseCase,
    private val quranAudioManager: QuranAudioManager,
    private val getAudioSettingsUseCase: GetAudioSettingsUseCase,
    private val getAudioFileUseCase: GetAudioFileUseCase,
    private val saveAudioSettingsUseCase: SaveAudioSettingsUseCase,
    private val getTranslationListUseCase: GetTranslationListUseCase,
    private val getAudioListUseCase: GetAudioListUseCase
    ) : ViewModel() {

    private val _quranDetailScreenState = MutableStateFlow(QuranDetailScreenState())
    val quranDetailScreenState = _quranDetailScreenState

    private val _audioPlayerState = MutableStateFlow(AudioPlayerState())
    val audioPlayerState = _audioPlayerState

    private val _quranSettingsState = MutableStateFlow(QuranSettingsState())
    val quranSettingsState = _quranSettingsState

    private val settings = getAudioSettingsUseCase()


    fun loadAudioList() = viewModelScope.launch(Dispatchers.IO) {
        val audioResponse = getAudioListUseCase()
        _quranSettingsState.value = _quranSettingsState.value.copy(isLoading = true)
        when (audioResponse.status) {
            Status.SUCCESS -> {
                _quranSettingsState.update {
                    it.copy(
                        reciterList = audioResponse.data!!,
                        selectedReciter = audioResponse.data[0].toText(),
                        selectedReciterIndex = 0,
                        isError = null,
                        isLoading = false
                    )
                }
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

    suspend fun getSelectedSurah() {
        _quranDetailScreenState.update { it.copy(isLoading = true) }
        val quranScreenState = _quranSettingsState.value
        val surahNumber = quranDetailScreenState.value.selectedSurahNumber
        if (quranScreenState.reciterList.isEmpty() || quranScreenState.transliterationList.isEmpty()) return
        val recitePathIdentifier = _quranSettingsState.value.reciterList[_quranSettingsState.value.selectedReciterIndex].identifier
        val transliterationPath = _quranSettingsState.value.transliterationList[_quranSettingsState.value.selectedTransliteration]
        val surahResponse = getSelectedSurahUseCase(surahNumber, "$recitePathIdentifier,$transliterationPath")
        when (surahResponse.status) {
            Status.SUCCESS -> {
                _quranDetailScreenState.value = _quranDetailScreenState.value.copy(
                    selectedSurah = surahResponse.data!!,
                    isError = null,
                    isLoading = false,
                    selectedAyahNumber = 1
                )
                if (_audioPlayerState.value.audioPlaying) {
                    updateCurrentAyahNumber(0)
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

    fun updateSurahNumber(surahNumber : Int) = viewModelScope.launch {
        quranDetailScreenState.update {
            it.copy(selectedSurahNumber = surahNumber)
        }
    }

    fun updateCurrentAyahNumber(direction: Int)  {
        pauseAudio()
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

    fun updateSelectedAyahNumber(ayahNumber : Int) = viewModelScope.launch {
        pauseAudio()
        _quranDetailScreenState.update {
            it.copy(selectedAyahNumber = ayahNumber)
        }
        println(ayahNumber)
        playAyahAudio()
    }


    fun playAyahAudio() {
        val selectedSurah = _quranDetailScreenState.value.selectedSurah ?: return
        val ayah = selectedSurah.ayahs?.get(_quranDetailScreenState.value.selectedAyahNumber - 1) ?: return
        val shouldCacheAudio = _quranSettingsState.value.shouldCacheAudio
        val audioUrl = ayah.audio
        val reciteLink = _quranSettingsState.value.reciterList[_quranSettingsState.value.selectedReciterIndex].identifier
        val reciteName = _quranSettingsState.value.selectedReciter.substringAfter('-')
        quranAudioManager.setCurrentAudioInfo(selectedSurah.englishName,ayah.number,reciteLink,reciteName,quranSettingsState.value.shouldCacheAudio,quranSettingsState.value.playbackSpeed)
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

    fun setPlaybackSpeed(speed : Float){
        quranAudioManager.setPlaybackSpeed(speed)
    }

    fun seekTo(position: Float) {
        _audioPlayerState.update { it.copy(currentAudioPosition = position) }
        quranAudioManager.seekTo(position)
    }

    private fun setupAudioPlayerCallbacks() {
        quranAudioManager.setProgressCallback { position, duration ->
            _audioPlayerState.update { it.copy(currentAudioPosition = position, audioDuration = duration) }
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

    // ... existing code ...
    fun onSettingsEvent(event: QuranDetailScreenEvent) = viewModelScope.launch {
        when (event) {
            is QuranDetailScreenEvent.ToggleAutoHidePlayer -> {
                saveAudioSettingsUseCase(settings.first().copy(autoHidePlayer = !settings.first().autoHidePlayer))

            }
            is QuranDetailScreenEvent.SetPlaybackSpeed -> {
                saveAudioSettingsUseCase(settings.first().copy(playbackSpeed = event.speed))
                setPlaybackSpeed(event.speed)
            }
            is QuranDetailScreenEvent.TogglePlaybackMode -> {
                _quranSettingsState.update { it.copy(playByVerse = !it.playByVerse) }
            }
            is QuranDetailScreenEvent.SetShouldCacheAudio -> {
                saveAudioSettingsUseCase(settings.first().copy(shouldCacheAudio = event.shouldCache))
            }
            is QuranDetailScreenEvent.ToggleSettingsSheet -> {
                _quranSettingsState.update { it.copy(showSettings = !it.showSettings) }
            }
            is QuranDetailScreenEvent.ToggleCacheInfoDialog -> {
                _quranSettingsState.update { it.copy(showCacheInfo = !it.showCacheInfo) }
            }
            is QuranDetailScreenEvent.SetTranslation -> {
                saveAudioSettingsUseCase(settings.first().copy(selectedTranslation = event.translation))
            }
            is QuranDetailScreenEvent.SetReciter -> {
                saveAudioSettingsUseCase(settings.first().copy(selectedReciter = event.reciter, selectedReciterIndex = event.reciterIndex))
            }
            is QuranDetailScreenEvent.SetTransliteration -> {
                saveAudioSettingsUseCase(settings.first().copy(selectedTranslation = event.transliteration))
            }
            is QuranDetailScreenEvent.SetFontSize -> {
                saveAudioSettingsUseCase(settings.first().copy(fontSize = event.size))
            }
            else -> Unit
        }
    }

    init {
        setupAudioPlayerCallbacks()
        loadAudioList()
        loadTranslationList()
        viewModelScope.launch(Dispatchers.IO){
            getAudioSettingsUseCase.invoke().collect {setting ->
                _quranSettingsState.update { it.copy(
                    shouldCacheAudio = setting.shouldCacheAudio,
                    selectedReciterIndex = if (setting.selectedReciterIndex != -1) setting.selectedReciterIndex else it.selectedReciterIndex,
                    selectedReciter = if (setting.selectedReciter.isNotEmpty()) setting.selectedReciter else it.selectedReciter,
                    selectedTranslation = if (setting.selectedTranslation.isNotEmpty()) setting.selectedTranslation else it.selectedTranslation,
                    autoHidePlayer = setting.autoHidePlayer,
                    playbackSpeed = setting.playbackSpeed,
                    fontSize = setting.fontSize,
                    selectedTransliteration = if (setting.selectedTransliteration.isNotEmpty()) setting.selectedTransliteration else it.selectedTransliteration
                ) }
            }
        }
        viewModelScope.launch(Dispatchers.IO) {
            _quranSettingsState
                .filter {
                    it.reciterList.isNotEmpty() && it.transliterationList.isNotEmpty()
                }
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
                        getSelectedSurah()
                    }
                }
        }
        viewModelScope.launch(Dispatchers.IO) {
            _quranDetailScreenState.mapLatest { it.selectedSurahNumber }.distinctUntilChanged().collect{
                getSelectedSurah()
            }
        }
    }

    private fun releaseCallbacks() {
        quranAudioManager.setProgressCallback(null)
        quranAudioManager.setErrorCallback(null)
        quranAudioManager.setIsPlayingCallback(null)
        quranAudioManager.setAyahChangedCallback(null)
    }

    override fun onCleared() {
        super.onCleared()
        releaseCallbacks()
        //quranAudioManager.stopAudio()
       // quranAudioManager.releaseMediaPlayer()
    }

}