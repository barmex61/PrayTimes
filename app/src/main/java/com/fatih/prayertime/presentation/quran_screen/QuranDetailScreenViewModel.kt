package com.fatih.prayertime.presentation.quran_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fatih.prayertime.data.audio.AudioStateManager
import com.fatih.prayertime.data.audio.QuranAudioManager
import com.fatih.prayertime.domain.use_case.quran_use_cases.GetAudioListUseCase
import com.fatih.prayertime.domain.use_case.quran_use_cases.GetSelectedSurahUseCase
import com.fatih.prayertime.domain.use_case.quran_use_cases.GetTranslationListUseCase
import com.fatih.prayertime.domain.use_case.settings_use_cases.GetQuranMediaSettingsUseCase
import com.fatih.prayertime.domain.use_case.settings_use_cases.SaveQuranMediaSettingsUseCase
import com.fatih.prayertime.util.extensions.toText
import com.fatih.prayertime.util.model.enums.PlaybackMode
import com.fatih.prayertime.util.model.event.QuranDetailScreenEvent
import com.fatih.prayertime.util.model.state.AudioInfo
import com.fatih.prayertime.util.model.state.quran_detail.QuranDetailScreenState
import com.fatih.prayertime.util.model.state.quran_detail.QuranSettingsState
import com.fatih.prayertime.util.model.state.Status
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.collections.first
import java.io.IOException
import java.net.SocketTimeoutException

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class QuranDetailScreenViewModel @Inject constructor(
    private val getSelectedSurahUseCase: GetSelectedSurahUseCase,
    private val quranAudioManager: QuranAudioManager,
    private val getQuranMediaSettingsUseCase: GetQuranMediaSettingsUseCase,
    private val saveQuranMediaSettingsUseCase: SaveQuranMediaSettingsUseCase,
    private val getTranslationListUseCase: GetTranslationListUseCase,
    private val getAudioListUseCase: GetAudioListUseCase,
    private val audioStateManager: AudioStateManager
) : ViewModel() {

    private val _quranDetailScreenState = MutableStateFlow(QuranDetailScreenState())
    val quranDetailScreenState = _quranDetailScreenState.asStateFlow()

    val audioPlayerState = audioStateManager.audioPlayerState

    private val _quranSettingsState = MutableStateFlow(QuranSettingsState())
    val quranSettingsState = _quranSettingsState.asStateFlow()

    private var audioDownloadJob: Job? = null

    private val settings = getQuranMediaSettingsUseCase()


    fun loadAudioList() = viewModelScope.launch(Dispatchers.IO) {
        val audioResponse = getAudioListUseCase()
        _quranSettingsState.update {  it.copy(isLoading = true) }
        _quranSettingsState.update { quranSettingsState->
            when (audioResponse.status) {
                Status.SUCCESS -> {
                    quranSettingsState.copy(
                        reciterList = audioResponse.data!!,
                        selectedReciter =
                            if (quranSettingsState.selectedReciter.isEmpty()) audioResponse.data.first().toText()
                            else quranSettingsState.selectedReciter,
                        selectedReciterIndex = 0,
                        isError = null,
                        isLoading = false
                    )
                }
                Status.ERROR -> {
                    quranSettingsState.copy(
                        isError = audioResponse.message!!,
                        isLoading = false
                    )
                }
                else -> {
                    quranSettingsState.copy(
                        isError = null,
                        isLoading = true
                    )
                }
            }
        }
    }

    fun loadTranslationList() = viewModelScope.launch(Dispatchers.IO) {
        val translationResponse = getTranslationListUseCase()
        _quranSettingsState.update {it.copy(isLoading = true) }
        _quranSettingsState.update { state->
            when (translationResponse.status) {
                Status.SUCCESS -> state.copy(
                    translationList = translationResponse.data!!,
                    selectedTranslation =
                        if (state.selectedTranslation.isEmpty()) translationResponse.data.first().toText()
                        else state.selectedTranslation,
                    isError = null,
                    isLoading = false
                )
                Status.ERROR -> state.copy(
                    isError = translationResponse.message,
                    isLoading = false
                )
                else -> state.copy(
                    isError = null,
                    isLoading = true
                )
            }
        }
    }

    private fun getSurahPath() : String? {
        if (quranSettingsState.value.reciterList.isEmpty() || quranSettingsState.value.transliterationList.isEmpty()) return null
        val recitePathIdentifier = _quranSettingsState.value.reciterList[_quranSettingsState.value.selectedReciterIndex].identifier
        val transliterationPath = _quranSettingsState.value.transliterationList[_quranSettingsState.value.selectedTransliteration] ?: return null
        val translationPath = _quranSettingsState.value.translationList.firstOrNull{it.toText() == _quranSettingsState.value.selectedTranslation}?.identifier ?:return null
        val surahPath = "$recitePathIdentifier,$transliterationPath,$translationPath"
        return surahPath
    }

    fun getSelectedSurah() = viewModelScope.launch(Dispatchers.IO) {
        _quranDetailScreenState.update { it.copy(isLoading = true) }
        val surahNumber = quranDetailScreenState.value.selectedSurahNumber
        val surahPath = getSurahPath() ?: return@launch
        val surahResponse = getSelectedSurahUseCase(surahNumber, surahPath)
        _quranDetailScreenState.value =
            when (surahResponse.status) {
                Status.SUCCESS -> {
                    _quranDetailScreenState.value.copy(
                        selectedSurah = surahResponse.data!!,
                        isError = null,
                        isLoading = false,
                        selectedAyahNumber = 1
                    )
                }
                Status.ERROR -> {
                    _quranDetailScreenState.value.copy(
                        isError = surahResponse.message!!,
                        isLoading = false,
                        selectedAyahNumber = 1
                    )
                }

                Status.LOADING -> {
                    _quranDetailScreenState.value.copy(
                        isError = null,
                        isLoading = true,
                        selectedAyahNumber = 1
                    )
                }
            }
    }

    fun updateSurahNumber(surahNumber : Int) = viewModelScope.launch {
        stopAudio()
        _quranDetailScreenState.value = _quranDetailScreenState.value.copy(selectedSurahNumber = surahNumber)
    }

    fun cancelAudioDownload()  {
        quranAudioManager.cancelAudioDownload()
    }

    fun updateCurrentAudioNumber(direction: Int, directAudioNumber : Int? = null) {
        if (audioPlayerState.value.isLoading || audioPlayerState.value.error != null) {
            audioDownloadJob?.cancel()
            audioStateManager.updateState { 
                copy(
                    isLoading = false,
                    downloadProgress = 0,
                    downloadedSize = 0,
                    totalSize = 0
                )
            }
        }

        pauseAudio()

        val selectedSurah = _quranDetailScreenState.value.selectedSurah ?: return
        val ayahSize = selectedSurah.ayahs?.size ?: return

        if (_quranDetailScreenState.value.selectedAyahNumber == ayahSize && direction == 1) {
            audioStateManager.updateState { copy(isLoading = false) }
            return
        }

        when(_quranSettingsState.value.playbackMode){
            PlaybackMode.VERSE_STREAM -> {
                val updatedAyahNumber = directAudioNumber ?: (_quranDetailScreenState.value.selectedAyahNumber + direction).coerceIn(1, ayahSize)
                _quranDetailScreenState.value = _quranDetailScreenState.value.copy(selectedAyahNumber = updatedAyahNumber)
            }
            PlaybackMode.SURAH -> {
                val updatedSurahNumber = (_quranDetailScreenState.value.selectedSurahNumber + direction).coerceIn(1, 114)
                _quranDetailScreenState.value = _quranDetailScreenState.value.copy(selectedSurahNumber = updatedSurahNumber)
                if (directAudioNumber != null) return
            }
        }

        downloadAndPlayAudio()
    }


    fun downloadAndPlayAudio() = viewModelScope.launch(Dispatchers.IO){
        cancelAudioDownload()
        val selectedSurah = _quranDetailScreenState.value.selectedSurah ?: return@launch
        val ayah = selectedSurah.ayahs?.get(_quranDetailScreenState.value.selectedAyahNumber - 1) ?: return@launch
        val shouldCacheAudio = _quranSettingsState.value.shouldCacheAudio
        val reciteLink = _quranSettingsState.value.reciterList[_quranSettingsState.value.selectedReciterIndex].identifier
        val reciteName = _quranSettingsState.value.selectedReciter.substringAfter('-')
        val playbackMode = _quranSettingsState.value.playbackMode
        val playbackSpeed = _quranSettingsState.value.playbackSpeed
        var audioPath = ""
        var (audioNumber,bitRate) = when(playbackMode){
            PlaybackMode.SURAH -> {
                val rate = 128
                audioPath = "audio-surah"
                selectedSurah.number to rate
            }
            PlaybackMode.VERSE_STREAM -> {
                val rate = ayah.audio.substringAfter("audio/").substringBefore('/').toInt()
                audioPath = "audio"
                ayah.number to rate
            }
        }
        val audioInfo = AudioInfo(
            selectedSurah.englishName,
            audioNumber,
            reciteLink,
            reciteName,
            bitRate,
            playbackMode,
            audioPath,
            playbackSpeed,
            shouldCacheAudio
        )

        audioStateManager.updateState {
            copy(
                isLoading = true,
                downloadProgress = 0,
                downloadedSize = 0,
                totalSize = 0,
                currentAudioInfo = audioInfo,
                error = null
            )
        }
        quranAudioManager.downloadAndPlayAudio()

    }

    fun pauseAudio() {
        quranAudioManager.pauseAudio()
    }

    fun resumeAudio() {
        val currentAudioInfo = audioPlayerState.value.currentAudioInfo
        val currentSurahNumber = _quranDetailScreenState.value.selectedSurahNumber
        val currentAyahNumber = _quranDetailScreenState.value.selectedAyahNumber

        if (currentAudioInfo == null || 
            (currentAudioInfo.playbackMode == PlaybackMode.VERSE_STREAM && currentAudioInfo.audioNumber != currentAyahNumber) ||
            (currentAudioInfo.playbackMode == PlaybackMode.SURAH && currentAudioInfo.audioNumber != currentSurahNumber)) {
            downloadAndPlayAudio()
            println("downloadAndPlay")
            return
        }
        println("resume")
        quranAudioManager.resumeAudio()
    }

    fun stopAudio() {
        quranAudioManager.stopAudio()
    }

    fun setPlaybackSpeed(speed : Float){
        resumeAudio()
    }

    fun seekTo(position: Float) {
        quranAudioManager.seekTo(position)
    }



    fun onSettingsEvent(event: QuranDetailScreenEvent) = viewModelScope.launch {
        when (event) {
            is QuranDetailScreenEvent.ToggleAutoHidePlayer -> {
                saveQuranMediaSettingsUseCase(settings.first().copy(autoHidePlayer = !settings.first().autoHidePlayer))
            }
            is QuranDetailScreenEvent.ToggleAutoScrollAyah ->{
                saveQuranMediaSettingsUseCase(settings.first().copy(autoScrollAyah = !settings.first().autoScrollAyah))
            }
            is QuranDetailScreenEvent.PlayAyahWithDoubleClick ->{
                saveQuranMediaSettingsUseCase(settings.first().copy(playAyahWithDoubleClick = !settings.first().playAyahWithDoubleClick))
            }
            is QuranDetailScreenEvent.SetPlaybackSpeed -> {
                saveQuranMediaSettingsUseCase(settings.first().copy(playbackSpeed = event.speed))
                setPlaybackSpeed(event.speed)
            }
            is QuranDetailScreenEvent.SetShouldCacheAudio -> {
                saveQuranMediaSettingsUseCase(settings.first().copy(shouldCacheAudio = event.shouldCache))
            }
            is QuranDetailScreenEvent.ToggleSettingsSheet -> {
                _quranSettingsState.update { it.copy(showSettings = !it.showSettings) }
            }
            is QuranDetailScreenEvent.ToggleCacheInfoDialog -> {
                _quranSettingsState.update { it.copy(showCacheInfo = !it.showCacheInfo) }
            }
            is QuranDetailScreenEvent.SetTranslation -> {
                saveQuranMediaSettingsUseCase(settings.first().copy(selectedTranslation = event.translation))
            }
            is QuranDetailScreenEvent.SetReciter -> {
                saveQuranMediaSettingsUseCase(settings.first().copy(selectedReciter = event.reciter, selectedReciterIndex = event.reciterIndex))
            }
            is QuranDetailScreenEvent.SetTransliteration -> {
                saveQuranMediaSettingsUseCase(settings.first().copy(selectedTransliteration = event.transliteration))
            }
            is QuranDetailScreenEvent.SetFontSize -> {
                saveQuranMediaSettingsUseCase(settings.first().copy(fontSize = event.size))
            }
            is QuranDetailScreenEvent.TogglePlaybackMode -> {
                saveQuranMediaSettingsUseCase(settings.first().copy(playbackMode = if (settings.first().playbackMode == PlaybackMode.VERSE_STREAM) PlaybackMode.SURAH else PlaybackMode.VERSE_STREAM))
            }
            else -> Unit
        }
    }

    init {
        loadAudioList()
        loadTranslationList()
        viewModelScope.launch(Dispatchers.IO) {
            getQuranMediaSettingsUseCase().collect { setting ->
                _quranSettingsState.update {
                    it.copy(
                        shouldCacheAudio = setting.shouldCacheAudio,
                        selectedReciterIndex = if (setting.selectedReciterIndex != -1) setting.selectedReciterIndex else it.selectedReciterIndex,
                        selectedReciter = if (setting.selectedReciter.isNotEmpty()) setting.selectedReciter else it.selectedReciter,
                        selectedTranslation = if (setting.selectedTranslation.isNotEmpty()) setting.selectedTranslation else it.selectedTranslation,
                        autoHidePlayer = setting.autoHidePlayer,
                        autoScrollAyah = setting.autoScrollAyah,
                        playAyahWithDoubleClick = setting.playAyahWithDoubleClick,
                        playbackSpeed = setting.playbackSpeed,
                        fontSize = setting.fontSize,
                        playbackMode = setting.playbackMode,
                        selectedTransliteration = if (setting.selectedTransliteration.isNotEmpty()) setting.selectedTransliteration else it.selectedTransliteration
                    )
                }
            }
        }
        viewModelScope.launch(Dispatchers.IO){
            _quranSettingsState.combine(_quranDetailScreenState){ settings,detail ->
                    Pair(settings,detail)
                }.filter { pair ->
                    pair.first.reciterList.isNotEmpty() &&
                    pair.first.transliterationList.isNotEmpty() &&
                    pair.first.translationList.isNotEmpty() &&
                    pair.first.selectedTranslation.isNotEmpty() &&
                    pair.first.selectedTransliteration.isNotEmpty() &&
                    pair.first.selectedReciter.isNotEmpty() &&
                    pair.second.selectedSurahNumber != 0
                }
                .map { pair ->
                    FlowData(
                        pair.first.selectedTranslation,
                        pair.first.selectedTransliteration,
                        pair.first.selectedReciter,
                        pair.second.selectedSurahNumber
                    )
                }
                .distinctUntilChanged{old,new ->
                    old.first == new.first &&
                    old.second == new.second &&
                    old.third == new.third &&
                    old.fourth == new.fourth
                }
                .collectLatest { triple ->
                    audioStateManager.updateState { 
                        copy(
                            isLoading = false,
                            isPlaying = false,
                            duration = 0f,
                            currentAudioInfo = null
                        )
                    }
                    audioDownloadJob?.cancel()
                    getSelectedSurah()
                }
        }
        viewModelScope.launch(Dispatchers.Default){
            audioPlayerState.map {
                it.currentAudioInfo!!.audioNumber
            }
                .distinctUntilChanged()
                .collect {
                _quranDetailScreenState.value = _quranDetailScreenState.value.copy(selectedAyahNumber = it)
            }
        }
    }


    override fun onCleared() {
        super.onCleared()
        cancelAudioDownload()
        //quranAudioManager.stopAudio()
       // quranAudioManager.releaseMediaPlayer()
    }

    private data class FlowData(
        val first : String,
        val second : String,
        val third : String,
        val fourth : Int
    )

}