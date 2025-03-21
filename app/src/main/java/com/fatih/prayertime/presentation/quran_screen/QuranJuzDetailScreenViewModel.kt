package com.fatih.prayertime.presentation.quran_screen

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fatih.prayertime.data.audio.QuranAudioManager
import com.fatih.prayertime.data.remote.dto.qurandto.Ayah
import com.fatih.prayertime.domain.use_case.quran_use_cases.GetJuzListUseCase
import com.fatih.prayertime.domain.use_case.quran_use_cases.GetSelectedSurahUseCase
import com.fatih.prayertime.domain.use_case.quran_use_cases.GetSurahListUseCase
import com.fatih.prayertime.util.model.event.QuranJuzDetailScreenEvent
import com.fatih.prayertime.util.model.state.AudioPlayerState
import com.fatih.prayertime.util.model.state.QuranJuzDetailScreenState
import com.fatih.prayertime.util.model.state.quran_detail.QuranSettingsState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuranJuzDetailScreenViewModel @Inject constructor(
    private val getSurahDetailUseCase: GetSelectedSurahUseCase,
    private val getSurahListUseCase: GetSurahListUseCase,
    private val getJuzListUseCase: GetJuzListUseCase,
    private val quranAudioManager: QuranAudioManager,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val TAG = "QuranJuzDetailViewModel"

    private val _quranJuzDetailScreenState = MutableStateFlow(QuranJuzDetailScreenState())
    val quranJuzDetailScreenState = _quranJuzDetailScreenState.asStateFlow()

    private val _quranSettingsState = MutableStateFlow(QuranSettingsState())
    val quranSettingsState = _quranSettingsState.asStateFlow()

    private val _audioPlayerState = MutableStateFlow(AudioPlayerState())
    val audioPlayerState = _audioPlayerState


    private var audioDownloadJob: Job? = null

    init {
        setupAudioPlayerCallbacks()
        loadSettings()
    }

    private fun loadSettings() = viewModelScope.launch(Dispatchers.IO) {
        // Burada ayarları yükle
    }

    fun updateJuzNumber(juzNumber: Int) = viewModelScope.launch(Dispatchers.IO) {
        Log.d(TAG, "updateJuzNumber: $juzNumber")
        _quranJuzDetailScreenState.update { it.copy(selectedJuzNumber = juzNumber) }
        getSelectedJuz()
    }

    fun getSelectedJuz() = viewModelScope.launch {
        Log.d(TAG, "getSelectedJuz çağrıldı")
        _quranJuzDetailScreenState.update { it.copy(isLoading = true) }
        
        // Juz detaylarını al
        val juzNumber = quranJuzDetailScreenState.value.selectedJuzNumber
        val juzResponse = getJuzListUseCase()
        
        if (juzResponse.data != null) {
            val selectedJuz = juzResponse.data.firstOrNull { it.juzNumber == juzNumber }
            
            if (selectedJuz != null) {
                // Juz'a ait sûreleri al
                val surahListResponse = getSurahListUseCase()
                
                if (surahListResponse.data != null) {
                    val surahList = surahListResponse.data
                    
                    // TODO: Burada gerçek juz-surah eşleştirmesi yapılmalı
                    // Şimdilik sadece örnek verileri kullanıyoruz
                    val surahsInJuz = surahList.take(3) // Örnek olarak ilk 3 sureyi al
                    
                    _quranJuzDetailScreenState.update { state ->
                        state.copy(
                            isLoading = false,
                            isError = null,
                            selectedJuz = selectedJuz,
                            surahsInJuz = surahsInJuz,
                            selectedSurah = surahsInJuz.firstOrNull()
                        )
                    }
                    
                    // Eğer ilk sure varsa onu seç ve detaylarını yükle
                    if (surahsInJuz.isNotEmpty()) {
                        loadSurahDetails(surahsInJuz.first().number)
                    }
                } else {
                    _quranJuzDetailScreenState.update { it.copy(
                        isLoading = false,
                        isError = surahListResponse.message ?: "Sûre listesi alınamadı"
                    )}
                }
            } else {
                _quranJuzDetailScreenState.update { it.copy(
                    isLoading = false,
                    isError = "Belirtilen cüz bulunamadı"
                )}
            }
        } else {
            _quranJuzDetailScreenState.update { it.copy(
                isLoading = false,
                isError = juzResponse.message ?: "Cüz listesi alınamadı"
            )}
        }
    }
    
    private fun loadSurahDetails(surahNumber: Int) = viewModelScope.launch(Dispatchers.IO) {
        _quranJuzDetailScreenState.update { it.copy(selectedSurahNumber = surahNumber) }
        
        val surahDetailResponse = getSurahDetailUseCase(
            surahNumber = surahNumber,
            "${_quranSettingsState.value.selectedTranslation},${_quranSettingsState.value.selectedReciter}"
        )
        
        if (surahDetailResponse.data != null) {
            _quranJuzDetailScreenState.update { state ->
                state.copy(
                    selectedSurah = surahDetailResponse.data,
                    selectedAyahNumber = 1
                )
            }
        } else {
            _quranJuzDetailScreenState.update { it.copy(
                isError = surahDetailResponse.message ?: "Sûre detayları alınamadı"
            )
            }
        }
    }

    fun updateCurrentAyahNumber(direction: Int) {
        Log.d(TAG, "updateCurrentAyahNumber: direction=$direction")
        
        // Önceki audio işlemini iptal et
        audioDownloadJob?.cancel()
        audioDownloadJob = null
        
        // Audio oynatmayı durdur
        quranAudioManager.stopAudio()
        
        val selectedSurah = _quranJuzDetailScreenState.value.selectedSurah ?: return
        val ayahSize = selectedSurah.ayahs?.size ?: 0
        
        // Eğer son ayete gelindiyse ve ileri gidiliyorsa, işlem yapma
        if (_quranJuzDetailScreenState.value.selectedAyahNumber == ayahSize && direction == 1) {
            return
        }
        
        // Yeni ayet numarasını hesapla
        _quranJuzDetailScreenState.value = _quranJuzDetailScreenState.value.copy(
            selectedAyahNumber = (_quranJuzDetailScreenState.value.selectedAyahNumber + direction).coerceIn(
                1, ayahSize
            )
        )
        
        Log.d(TAG, "Yeni ayet numarası: ${_quranJuzDetailScreenState.value.selectedAyahNumber}")
        
        // Yarış koşulunu önlemek için kısa bir gecikme ekle
        viewModelScope.launch {
            delay(100)
            playAyahAudio()
        }
    }

    fun playAyahAudio() = viewModelScope.launch(Dispatchers.IO) {
        val selectedSurah = _quranJuzDetailScreenState.value.selectedSurah ?: return@launch
        val ayah = selectedSurah.ayahs?.get(_quranJuzDetailScreenState.value.selectedAyahNumber - 1) ?: return@launch
        
        playAyahAudio(ayah)
    }

    private fun playAyahAudio(ayah: Ayah) {
        audioDownloadJob?.cancel()
        
        audioDownloadJob = viewModelScope.launch(Dispatchers.IO) {
            val reciterName = _quranSettingsState.value.selectedReciter
            val shouldCache = _quranSettingsState.value.shouldCacheAudio
            val surahNumber = _quranJuzDetailScreenState.value.selectedSurah?.number ?: return@launch

            /*
            quranAudioManager.prepareAndPlayAudio(
                reciterName = reciterName,
                surahNumber = surahNumber,
                ayahNumber = ayah.numberInSurah,
                shouldCache = shouldCache,
                onPrepared = {
                    // Hazırlandığında
                }
            ) */
        }
    }

    private fun setupAudioPlayerCallbacks() {


        quranAudioManager.setProgressCallback { progress, duration ->
            _audioPlayerState.update {
                it.copy(
                    audioDuration = duration,
                    currentAudioPosition = progress
                )
            }
        }

        quranAudioManager.setErrorCallback { errorMessage ->
            _audioPlayerState.update {
                it.copy(
                    audioError = errorMessage,
                    audioLoading = false
                )
            }
        }

        quranAudioManager.setIsPlayingCallback { isPlaying ->
            _audioPlayerState.update {
                it.copy(
                    audioPlaying = isPlaying,
                    audioLoading = false
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
        quranAudioManager.seekTo(position)
    }

    fun onSettingsEvent(event: QuranJuzDetailScreenEvent) = viewModelScope.launch(Dispatchers.Default){
        when(event){
            is QuranJuzDetailScreenEvent.ToggleAutoHidePlayer -> {
                // Auto hide player ayarını güncelle
            }
            is QuranJuzDetailScreenEvent.SetPlaybackSpeed -> {
                // Oynatma hızını ayarla
            }
            is QuranJuzDetailScreenEvent.TogglePlaybackMode -> {
                // Oynatma modunu değiştir
            }
            is QuranJuzDetailScreenEvent.SetShouldCacheAudio -> {
                // Ses önbellekleme ayarını güncelle
            }
            is QuranJuzDetailScreenEvent.ToggleSettingsSheet -> {
                // Ayarlar sayfasını aç/kapat
                _quranSettingsState.update { it.copy(showSettings = !it.showSettings) }
            }
            is QuranJuzDetailScreenEvent.ToggleCacheInfoDialog -> {
                // Önbellek bilgi dialogunu aç/kapat
            }
            is QuranJuzDetailScreenEvent.SetTranslation -> {
                // Çeviri ayarını güncelle
            }
            is QuranJuzDetailScreenEvent.SetReciter -> {
                // Okuyucu ayarını güncelle
            }
            is QuranJuzDetailScreenEvent.SetTransliteration -> {
                // Transliterasyon ayarını güncelle
            }
            is QuranJuzDetailScreenEvent.SelectSurah -> {
                // Seçilen sureyi değiştir
                loadSurahDetails(event.surahNumber)
            }
            else -> {}
        }
    }

    private fun releaseCallbacks() {
        quranAudioManager.setProgressCallback(null)
        quranAudioManager.setAyahChangedCallback(null)
        quranAudioManager.setIsPlayingCallback(null)
        quranAudioManager.setErrorCallback(null)
    }

    override fun onCleared() {
        super.onCleared()
        releaseCallbacks()
        audioDownloadJob?.cancel()
    }
} 