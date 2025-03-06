package com.fatih.prayertime.presentation.hadith_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fatih.prayertime.data.remote.dto.hadithdto.HadithEdition
import com.fatih.prayertime.domain.use_case.hadith_use_cases.GetHadithEditionsUseCase
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

    fun getHadithEditions() = viewModelScope.launch(Dispatchers.IO){
        _hadithEditions.emit(getHadithEditionsUseCase())

    }

    init {
        viewModelScope.launch(Dispatchers.IO){
            getHadithEditions()
        }
    }
}