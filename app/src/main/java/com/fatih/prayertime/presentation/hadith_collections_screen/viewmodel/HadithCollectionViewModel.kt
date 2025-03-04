package com.fatih.prayertime.presentation.hadith_collections_screen.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fatih.prayertime.data.remote.dto.hadithdto.HadithCollection
import com.fatih.prayertime.domain.model.HadithSectionCardData
import com.fatih.prayertime.domain.use_case.hadith_use_cases.get_hadith_collections_use_case.GetHadithCollectionsUseCase
import com.fatih.prayertime.util.Resource
import com.fatih.prayertime.util.Status
import com.fatih.prayertime.util.combineSectionsAndDetails
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HadithCollectionViewModel @Inject constructor(
    private val getHadithCollectionsUseCase: GetHadithCollectionsUseCase
)  : ViewModel(){

    private val _hadithCollectionPath : MutableStateFlow<String> = MutableStateFlow("")
    val hadithCollectionPath = _hadithCollectionPath

    private val _hadithCollection : MutableStateFlow<Resource<HadithCollection>> = MutableStateFlow(Resource.loading())
    val hadithCollection = _hadithCollection

    private val _hadithSectionCardDataList : MutableStateFlow<Resource<List<HadithSectionCardData>>> = MutableStateFlow(Resource.loading())
    val hadithSectionCardDataList = _hadithSectionCardDataList

    private fun getHadithCollection(collectionPath : String) = viewModelScope.launch(Dispatchers.IO){
        _hadithCollection.emit(Resource.loading())
        _hadithCollection.emit(getHadithCollectionsUseCase(collectionPath))
    }

    fun updateHadithCollectionPath(collectionPath: String) = viewModelScope.launch(Dispatchers.Default){
        _hadithCollectionPath.emit(collectionPath)
    }

    init {
        viewModelScope.launch(Dispatchers.IO){
            _hadithCollectionPath.collectLatest { path ->
                if (path.isNotEmpty()) { // Boş path'leri gereksiz yere işlememek için
                    Log.d("HadithCollectionViewModel", "Collection path: $path")
                    getHadithCollection(path)
                }
            }
        }
        viewModelScope.launch(Dispatchers.IO){
            _hadithCollection.collectLatest { hadithCollection
                when(hadithCollection.value.status){
                    Status.ERROR ->{
                        println(hadithCollection.value.message)
                        _hadithSectionCardDataList.emit(Resource.error(hadithCollection.value.message?:""))
                    }
                    Status.LOADING ->{
                        _hadithSectionCardDataList.emit(Resource.loading())
                    }
                    Status.SUCCESS ->{
                        _hadithSectionCardDataList.emit(
                            Resource.success(
                                combineSectionsAndDetails(
                                    hadithCollection.value.data!!.metadata.sections,
                                    hadithCollection.value.data!!.metadata.section_details)
                            )
                        )
                    }
                }
            }
        }
    }
}