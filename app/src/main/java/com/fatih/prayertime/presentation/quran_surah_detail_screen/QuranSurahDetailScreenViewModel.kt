package com.fatih.prayertime.presentation.quran_surah_detail_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fatih.prayertime.data.audio.AudioStateManager
import com.fatih.prayertime.data.audio.QuranAudioManager
import com.fatih.prayertime.domain.use_case.quran_use_cases.GetRecitersUseCase
import com.fatih.prayertime.domain.use_case.quran_use_cases.GetSelectedSurahUseCase
import com.fatih.prayertime.domain.use_case.quran_use_cases.GetTranslationListUseCase
import com.fatih.prayertime.domain.use_case.settings_use_cases.GetQuranMediaSettingsUseCase
import com.fatih.prayertime.domain.use_case.settings_use_cases.SaveQuranMediaSettingsUseCase
import com.fatih.prayertime.util.extensions.toText
import com.fatih.prayertime.util.model.enums.PlaybackMode
import com.fatih.prayertime.util.model.event.AudioPlayerEvent
import com.fatih.prayertime.util.model.event.QuranDetailScreenEvent
import com.fatih.prayertime.util.model.state.QuranDetailScreenState
import com.fatih.prayertime.util.model.state.QuranSettingsState
import com.fatih.prayertime.util.model.state.Status
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.collections.first

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
@HiltViewModel
class QuranSurahDetailScreenViewModel @Inject constructor(
    private val getSelectedSurahUseCase: GetSelectedSurahUseCase,
    private val quranAudioManager: QuranAudioManager,
    private val getQuranMediaSettingsUseCase: GetQuranMediaSettingsUseCase,
    private val saveQuranMediaSettingsUseCase: SaveQuranMediaSettingsUseCase,
    private val getTranslationListUseCase: GetTranslationListUseCase,
    private val getRecitersUseCase: GetRecitersUseCase,
    private val audioStateManager: AudioStateManager
) : ViewModel() {

    private val _quranDetailScreenState = MutableStateFlow(QuranDetailScreenState())
    val quranDetailScreenState = _quranDetailScreenState.asStateFlow()

    val audioPlayerState = audioStateManager.audioPlayerState

    private val _quranSettingsState = MutableStateFlow(QuranSettingsState())
    val quranSettingsState = _quranSettingsState.asStateFlow()

    private val settings = getQuranMediaSettingsUseCase()


    fun loadReciters(playbackMode: PlaybackMode) = viewModelScope.launch(Dispatchers.IO) {
        val reciterResponse = if (playbackMode == PlaybackMode.SURAH) getRecitersUseCase.getSurahBySurahReciters() else getRecitersUseCase.getVerseByVerseReciters()
        _quranSettingsState.update {  it.copy(isLoading = true) }
        _quranSettingsState.update { quranSettingsState->
            when (reciterResponse.status) {
                Status.SUCCESS -> {
                    quranSettingsState.copy(
                        reciterList = reciterResponse.data!!,
                        selectedReciter = reciterResponse.data.first().toText(),
                        selectedReciterIndex = 0,
                        isError = null,
                        isLoading = false
                    )
                }
                Status.ERROR -> {
                    quranSettingsState.copy(
                        isError = reciterResponse.message!!,
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
        val surahNumber = _quranDetailScreenState.value.selectedSurahNumber
        val surahPath = getSurahPath() ?: return@launch
        val surahResponse = getSelectedSurahUseCase(surahNumber, surahPath)
        _quranDetailScreenState.value =
            when (surahResponse.status) {
                Status.SUCCESS -> {
                    val selectedSurah = surahResponse.data!!
                    val minAyahNumber = selectedSurah.ayahs!!.first().number
                    val audioPath = selectedSurah.ayahs.first().audio
                    audioStateManager.updateState {
                        copy(
                            currentAudioInfo = currentAudioInfo.copy(
                            surahName = selectedSurah.englishName,
                            ayahNumber = minAyahNumber,
                            bitrate = when(quranSettingsState.value.playbackMode) {
                                PlaybackMode.SURAH -> 128
                                PlaybackMode.VERSE_STREAM -> audioPath.substringAfter("audio/").substringBefore('/').toInt()
                            }
                        )
                        )
                    }
                    if (audioPlayerState.value.isPlaying){
                        onAudioPlayerEvent(AudioPlayerEvent.PlayExactAudioNumber(minAyahNumber))
                    }

                    _quranDetailScreenState.value.copy(
                        selectedSurah = selectedSurah,
                        selectedAyahNumber = minAyahNumber,
                        isError = null,
                        isLoading = false,
                    )
                }
                Status.ERROR -> {
                    _quranDetailScreenState.value.copy(
                        isError = surahResponse.message!!,
                        isLoading = false,
                    )
                }

                Status.LOADING -> {
                    _quranDetailScreenState.value.copy(
                        isError = null,
                        isLoading = true,
                    )
                }
            }
    }

    fun initSurahNumber(surahNumber : Int) = viewModelScope.launch {
        audioStateManager.updateState {copy(currentAudioInfo = currentAudioInfo.copy(surahNumber = surahNumber))}
        _quranDetailScreenState.update { it.copy(selectedSurahNumber = surahNumber) }
    }

    fun onAudioPlayerEvent(audioPlayerEvent : AudioPlayerEvent){
        when(audioPlayerEvent){
            is AudioPlayerEvent.CancelAudioDownload -> quranAudioManager.cancelAudioDownload()
            is AudioPlayerEvent.PauseAudio -> quranAudioManager.pauseAudio()
            is AudioPlayerEvent.PlayNextAudio -> quranAudioManager.playNext()
            is AudioPlayerEvent.PlayPreviousAudio -> quranAudioManager.playPrevious()
            is AudioPlayerEvent.ResumeAudio -> quranAudioManager.resumeAudio()
            is AudioPlayerEvent.SeekAudio -> quranAudioManager.seekTo(audioPlayerEvent.position)
            is AudioPlayerEvent.PlayExactAudioNumber -> quranAudioManager.getExactAudio(audioPlayerEvent.audioNumber)
            is AudioPlayerEvent.StopAudio -> quranAudioManager.stopAudio()
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
                quranAudioManager.setPlaybackSpeed(event.speed)
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
                onAudioPlayerEvent(AudioPlayerEvent.StopAudio)
            }
            else -> Unit
        }
    }

    init {
        loadTranslationList()
        viewModelScope.launch(Dispatchers.IO) {
            launch {
                _quranSettingsState.map { settings->
                    settings.playbackMode
                }.distinctUntilChanged().collectLatest { playbackMode ->
                    loadReciters(playbackMode)
                }
            }
            launch {
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

            launch {
                _quranSettingsState.combine(_quranDetailScreenState){settings,quranDetail->
                    settings to quranDetail
                }.filter {(settings,quranDetail)->
                    quranDetail.selectedSurahNumber != 0
                }
                    .map { (settings,quranDetail) ->
                        FlowData(
                            settings.selectedTranslation,
                            settings.selectedTransliteration,
                            settings.selectedReciter,
                            quranDetail.selectedSurahNumber
                        )
                    }
                    .distinctUntilChanged()
                    .collectLatest { triple ->
                        getSelectedSurah()
                    }
            }

            launch {
                _quranSettingsState.filter { settings ->
                    settings.reciterList.isNotEmpty()
                }.map { settings->

                    audioStateManager.audioPlayerState.value.currentAudioInfo.copy(
                        reciter = settings.reciterList[settings.selectedReciterIndex].identifier,
                        reciterName = settings.selectedReciter.substringAfter('-'),
                        playbackMode = settings.playbackMode,
                        audioPath = if(settings.playbackMode == PlaybackMode.SURAH) "audio-surah" else "audio",
                        playbackSpeed = settings.playbackSpeed,
                        shouldCacheAudio = settings.shouldCacheAudio,

                        )

                }.distinctUntilChanged().collectLatest {audioInfo->
                    audioStateManager.updateState {
                        copy(
                            currentAudioInfo = audioInfo
                        )
                    }
                }
            }
            launch {
                audioPlayerState.map {
                    Pair(it.currentAudioInfo.ayahNumber,it.currentAudioInfo.surahNumber)
                }
                    .distinctUntilChanged()
                    .debounce(200)
                    .collect {
                        _quranDetailScreenState.value = _quranDetailScreenState.value.copy(selectedAyahNumber = it.first, selectedSurahNumber = it.second)
                    }
            }
        }


    }


    override fun onCleared() {
        super.onCleared()
        //onAudioPlayerEvent(AudioPlayerEvent.CancelAudioDownload)
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