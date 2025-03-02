package com.fatih.prayertime.presentation.hadith_screen.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fatih.prayertime.data.remote.dto.hadithdto.HadithEdition
import com.fatih.prayertime.domain.use_case.get_hadith_editions_use_case.GetHadithEditionsUseCase
import com.fatih.prayertime.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HadithScreenViewModel @Inject constructor(
    private val getHadithEditionsUseCase: GetHadithEditionsUseCase
) : ViewModel(){

    private val _hadithEditions : MutableStateFlow<Resource<HadithEdition?>> = MutableStateFlow(Resource.loading())
    val hadithEditions = _hadithEditions

    init {
        viewModelScope.launch(Dispatchers.IO){
            _hadithEditions.emit(getHadithEditionsUseCase())
            Log.d("HadithScreenViewModel", "HadithScreenViewModel: ${hadithEditions.value}")
        }
    }
}