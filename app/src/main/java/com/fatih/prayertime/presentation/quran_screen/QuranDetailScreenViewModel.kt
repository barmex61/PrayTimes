package com.fatih.prayertime.presentation.quran_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fatih.prayertime.data.audio.QuranAudioManager
import com.fatih.prayertime.domain.use_case.quran_use_cases.GetAudioListUseCase
import com.fatih.prayertime.domain.use_case.quran_use_cases.GetSelectedSurahUseCase
import com.fatih.prayertime.domain.use_case.quran_use_cases.GetTranslationListUseCase
import com.fatih.prayertime.domain.use_case.quran_use_cases.GetAudioFileUseCase
import com.fatih.prayertime.domain.use_case.settings_use_cases.GetQuranMediaSettingsUseCase
import com.fatih.prayertime.domain.use_case.settings_use_cases.SaveQuranMediaSettingsUseCase
import com.fatih.prayertime.util.extensions.toText
import com.fatih.prayertime.util.model.enums.PlaybackMode
import com.fatih.prayertime.util.model.event.QuranDetailScreenEvent
import com.fatih.prayertime.util.model.state.AudioPlayerState
import com.fatih.prayertime.util.model.state.quran_detail.QuranDetailScreenState
import com.fatih.prayertime.util.model.state.quran_detail.QuranSettingsState
import com.fatih.prayertime.util.model.state.Status
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
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

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class QuranDetailScreenViewModel @Inject constructor(
    private val getSelectedSurahUseCase: GetSelectedSurahUseCase,
    private val quranAudioManager: QuranAudioManager,
    private val getQuranMediaSettingsUseCase: GetQuranMediaSettingsUseCase,
    private val getAudioFileUseCase: GetAudioFileUseCase,
    private val saveQuranMediaSettingsUseCase: SaveQuranMediaSettingsUseCase,
    private val getTranslationListUseCase: GetTranslationListUseCase,
    private val getAudioListUseCase: GetAudioListUseCase
    ) : ViewModel() {

    private val _quranDetailScreenState = MutableStateFlow(QuranDetailScreenState())
    val quranDetailScreenState = _quranDetailScreenState

    private val _audioPlayerState = MutableStateFlow(AudioPlayerState())
    val audioPlayerState = _audioPlayerState

    private val _quranSettingsState = MutableStateFlow(QuranSettingsState())
    val quranSettingsState = _quranSettingsState

    private var audioDownloadJob : Job? = null

    private val settings = getQuranMediaSettingsUseCase()


    fun loadAudioList() = viewModelScope.launch(Dispatchers.IO) {
        val audioResponse = getAudioListUseCase()
        _quranSettingsState.update {  it.copy(isLoading = true) }
        _quranSettingsState.update { state->
            when (audioResponse.status) {
                Status.SUCCESS -> {
                    state.copy(
                        reciterList = audioResponse.data!!,
                        selectedReciter =
                            if (state.selectedReciter.isEmpty()) audioResponse.data.first().toText()
                            else state.selectedReciter,
                        selectedReciterIndex = 0,
                        isError = null,
                        isLoading = false
                    )
                }
                Status.ERROR -> {
                    state.copy(
                        isError = audioResponse.message!!,
                        isLoading = false
                    )
                }
                else -> {
                    state.copy(
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
        quranSettingsState.update { state->
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

    fun getSelectedSurah() = viewModelScope.launch(Dispatchers.IO) {
        _quranDetailScreenState.update { it.copy(isLoading = true) }
        val quranScreenState = _quranSettingsState.value
        val surahNumber = quranDetailScreenState.value.selectedSurahNumber
        if (quranScreenState.reciterList.isEmpty() || quranScreenState.transliterationList.isEmpty()) return@launch
        val recitePathIdentifier = _quranSettingsState.value.reciterList[_quranSettingsState.value.selectedReciterIndex].identifier
        val transliterationPath = _quranSettingsState.value.transliterationList[_quranSettingsState.value.selectedTransliteration]
        val translationPath = _quranSettingsState.value.translationList.firstOrNull{it.toText() == _quranSettingsState.value.selectedTranslation}?.identifier ?:return@launch
        val surahResponse = getSelectedSurahUseCase(surahNumber, "$recitePathIdentifier,$transliterationPath,$translationPath")
        _quranDetailScreenState.value =
            when (surahResponse.status) {
                Status.SUCCESS -> {
                   val state = _quranDetailScreenState.value.copy(
                        selectedSurah = surahResponse.data!!,
                        isError = null,
                        isLoading = false,
                        selectedAyahNumber = 1
                    )
                    _quranDetailScreenState.value = state
                    if (_audioPlayerState.value.isPlaying) {
                        updateCurrentAudioNumber(0)
                    }
                    state
                }

                Status.ERROR -> {
                    _quranDetailScreenState.value.copy(
                        isError = surahResponse.message!!,
                        isLoading = false
                    )
                }

                else -> {
                    _quranDetailScreenState.value.copy(
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

    fun cancelAudioDownload() = viewModelScope.launch(Dispatchers.IO) {
        audioDownloadJob?.cancel()
        audioDownloadJob = null
        _audioPlayerState.value = _audioPlayerState.value.copy(
            isLoading = false,
            downloadProgress = 0,
            downloadedSize = 0,
            totalSize = 0
        )
    }

    fun updateCurrentAudioNumber(direction: Int, directAudioNumber : Int? = null) {
        if (_audioPlayerState.value.isLoading || _audioPlayerState.value.error != null) {
            audioDownloadJob?.cancel()
            _audioPlayerState.value = _audioPlayerState.value.copy(
                isLoading = false,
                error = null,
                downloadProgress = 0,
                downloadedSize = 0,
                totalSize = 0
            )
        }

        pauseAudio()

        val selectedSurah = _quranDetailScreenState.value.selectedSurah ?: return
        val ayahSize = selectedSurah.ayahs?.size ?: return

        if (_quranDetailScreenState.value.selectedAyahNumber == ayahSize && direction == 1) {
            _audioPlayerState.update { it.copy(isLoading = false) }
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


    fun downloadAndPlayAudio() {
        audioDownloadJob?.cancel()
        
        val selectedSurah = _quranDetailScreenState.value.selectedSurah ?: return
        val ayah = selectedSurah.ayahs?.get(_quranDetailScreenState.value.selectedAyahNumber - 1) ?: return
        val shouldCacheAudio = _quranSettingsState.value.shouldCacheAudio
        val reciteLink = _quranSettingsState.value.reciterList[_quranSettingsState.value.selectedReciterIndex].identifier
        val reciteName = _quranSettingsState.value.selectedReciter.substringAfter('-')
        val playbackMode = _quranSettingsState.value.playbackMode
        var bitrate = 0

        val (audioPath, audioNumber) = when (playbackMode) {
            PlaybackMode.VERSE_STREAM -> {
                bitrate = ayah.audio.substringAfter("audio/").substringBefore('/').toInt()
                "audio" to ayah.number
            }
            PlaybackMode.SURAH -> {
                bitrate = 128
                "audio-surah" to selectedSurah.number.toInt()
            }
        }
        println(audioNumber)


        audioDownloadJob = viewModelScope.launch(Dispatchers.IO) {
            _audioPlayerState.value = _audioPlayerState.value.copy(
                isLoading = true,
                downloadProgress = 0,
                downloadedSize = 0,
                totalSize = 0
            )

            try {
                println("try")
                getAudioFileUseCase.invoke(audioPath, bitrate, reciteLink, audioNumber, shouldCacheAudio)
                    .collect { resource ->
                        if (!isActive) return@collect
                        
                        _audioPlayerState.value = when (resource.status) {
                            Status.LOADING -> {
                                println("loading")
                                _audioPlayerState.value.copy(
                                    isLoading = true,
                                    error = null,
                                    downloadProgress = resource.progress,
                                    downloadedSize = resource.downloadedSize,
                                    totalSize = resource.totalSize
                                )
                            }
                            Status.SUCCESS -> {
                                println("success")

                                quranAudioManager.setCurrentAudioInfo(
                                    selectedSurah.englishName,
                                    audioNumber,
                                    reciteLink,
                                    reciteName,
                                    shouldCacheAudio,
                                    _quranSettingsState.value.playbackSpeed,
                                    bitrate,
                                    playbackMode
                                )
                                quranAudioManager.playAudio(resource.data!!)
                                _audioPlayerState.value.copy(
                                    isLoading = true,
                                    error = null,
                                    downloadProgress = 100,
                                    downloadedSize = resource.totalSize,
                                    totalSize = resource.totalSize
                                )
                            }
                            Status.ERROR -> {
                                println("error")
                                _audioPlayerState.value.copy(
                                    error = resource.message,
                                    downloadProgress = 0,
                                    downloadedSize = 0,
                                    totalSize = 0
                                )
                            }
                        }
                    }
            } catch (e: Exception) {
                _audioPlayerState.value = _audioPlayerState.value.copy(error = e.message ?: "Ses dosyası oynatılamadı",
                    downloadProgress = 0,
                    downloadedSize = 0,
                    totalSize = 0
                )
            }
        }
    }

    fun pauseAudio() {
        quranAudioManager.pauseAudio()
    }

    fun resumeAudio() {
        _audioPlayerState.value.currentAudioInfo?.let { audioInfo ->
            val currentAudioNumber = when(audioInfo.playbackMode){
                PlaybackMode.VERSE_STREAM -> quranDetailScreenState.value.selectedAyahNumber
                else -> quranDetailScreenState.value.selectedSurahNumber
            }
            if (audioInfo.audioNumber == currentAudioNumber){
                quranAudioManager.resumeAudio { downloadAndPlayAudio() }
            }else downloadAndPlayAudio()
        }
    }

    fun stopAudio() {
        quranAudioManager.stopAudio()
    }

    fun setPlaybackSpeed(speed : Float){
        quranAudioManager.setPlaybackSpeed(speed)
    }

    fun seekTo(position: Float) {
        _audioPlayerState.update { it.copy(currentPosition = position) }
        quranAudioManager.seekTo(position)
    }

    private fun setupAudioPlayerCallbacks() {
        quranAudioManager.setProgressCallback { position, duration ->
            _audioPlayerState.update { it.copy(currentPosition = position, duration = duration) }
        }

        quranAudioManager.setAyahChangedCallback { direction ->
            updateCurrentAudioNumber(direction)
        }

        quranAudioManager.setErrorCallback { errorMessage ->
            _audioPlayerState.update { it.copy(error =errorMessage) }
        }

        quranAudioManager.setIsPlayingCallback { isPlaying ->
            _audioPlayerState.update { it.copy(isLoading = isPlaying)}
        }
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
                println(settings.first().playbackMode)
            }
            else -> Unit
        }
    }

    init {
        setupAudioPlayerCallbacks()
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
                    getSelectedSurah()
                    println("get")
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
        println("hey")
        releaseCallbacks()
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