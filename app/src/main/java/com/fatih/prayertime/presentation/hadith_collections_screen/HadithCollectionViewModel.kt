package com.fatih.prayertime.presentation.hadith_collections_screen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fatih.prayertime.data.local.entity.FavoritesEntity
import com.fatih.prayertime.data.remote.dto.hadithdto.Hadith
import com.fatih.prayertime.data.remote.dto.hadithdto.HadithCollection
import com.fatih.prayertime.domain.model.HadithSectionCardData
import com.fatih.prayertime.domain.use_case.favorites_use_cases.AddFavoriteUseCase
import com.fatih.prayertime.domain.use_case.favorites_use_cases.IsFavoriteUseCase
import com.fatih.prayertime.domain.use_case.favorites_use_cases.RemoveFavoriteUseCase
import com.fatih.prayertime.domain.use_case.hadith_use_cases.GetHadithCollectionsUseCase
import com.fatih.prayertime.util.FavoritesType
import com.fatih.prayertime.util.Resource
import com.fatih.prayertime.util.Status
import com.fatih.prayertime.util.combineSectionsAndDetails
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class HadithCollectionViewModel @Inject constructor(
    private val getHadithCollectionsUseCase: GetHadithCollectionsUseCase,
    private val isFavoriteUseCase: IsFavoriteUseCase,
    private val removeFavoriteUseCase: RemoveFavoriteUseCase,
    private val addFavoriteUseCase: AddFavoriteUseCase
)  : ViewModel(){

    private val hadithCollectionPath : MutableStateFlow<String> = MutableStateFlow("")
    private val hadithCollection : MutableStateFlow<Resource<HadithCollection>> = MutableStateFlow(Resource.loading())

    private val _hadithSectionCardDataList : MutableStateFlow<Resource<List<HadithSectionCardData>>> = MutableStateFlow(Resource.loading())
    val hadithSectionCardDataList = _hadithSectionCardDataList

    private val _selectedHadithSection : MutableStateFlow<HadithSectionCardData?> = MutableStateFlow(null)
    val selectedHadithSection = _selectedHadithSection

    private val _selectedHadithIndex : MutableStateFlow<Int> = MutableStateFlow(0)
    val selectedHadithIndex = _selectedHadithIndex


    fun getHadithCollection() = viewModelScope.launch(Dispatchers.IO){
        hadithCollection.emit(Resource.loading())
        hadithCollection.emit(getHadithCollectionsUseCase(hadithCollectionPath.value))
    }

    fun updateHadithCollectionPath(collectionPath: String) = viewModelScope.launch(Dispatchers.Default){
        hadithCollectionPath.emit(collectionPath)
    }

    fun updateSelectedHadithSection(hadithSectionCardData: HadithSectionCardData) = viewModelScope.launch(Dispatchers.Default){
        _selectedHadithSection.emit(hadithSectionCardData)
        _selectedHadithIndex.emit(0)
    }

    fun updateSelectedHadithIndex(hadithIndex : Int) = viewModelScope.launch(Dispatchers.Default){
        _selectedHadithIndex.emit(hadithIndex)
    }

    private val _isFavorite = MutableStateFlow(false)
    val isFavorite = _isFavorite.asStateFlow()

    fun checkIsFavorite(hadithId : Int) = viewModelScope.launch(Dispatchers.IO){
        _isFavorite.value = isFavoriteUseCase(hadithId, FavoritesType.DUA.name)
    }

    fun toggleFavorite(hadith: Hadith) = viewModelScope.launch(Dispatchers.IO){
        val id = try {
            hadith.hadithnumber.toInt()
        }catch (e:Exception){
            -1
        }
        if (_isFavorite.value) {
            println("remove")
            removeFavoriteUseCase(
                FavoritesEntity(
                    type = FavoritesType.HADIS.name,
                    title = hadith.text,
                    itemId = id,
                    content = "",
                )
            )
        } else {
            println("add")
            addFavoriteUseCase(
                FavoritesEntity(
                    type = FavoritesType.HADIS.name,
                    title = hadith.text,
                    itemId = id,
                    content ="",
                )
            )
        }
        _isFavorite.value = !_isFavorite.value
    }

    init {

        viewModelScope.launch(Dispatchers.IO){
            hadithCollectionPath.collectLatest { path ->
                if (path.isNotEmpty()) { // Boş path'leri gereksiz yere işlememek için
                    Log.d("HadithCollectionViewModel", "Collection path: $path")
                    getHadithCollection()
                }
            }
        }
        viewModelScope.launch(Dispatchers.IO){
            hadithCollection.collectLatest { hadithCollection
                when(hadithCollection.value.status){
                    Status.ERROR ->{
                        _hadithSectionCardDataList.emit(Resource.error(hadithCollection.value.message?:""))
                    }
                    Status.LOADING ->{
                        _hadithSectionCardDataList.emit(Resource.loading())
                    }
                    Status.SUCCESS ->{
                        _hadithSectionCardDataList.emit(Resource.success(combineSectionsAndDetails(hadithCollection.value.data!!)))
                    }
                }
            }
        }
    }

}