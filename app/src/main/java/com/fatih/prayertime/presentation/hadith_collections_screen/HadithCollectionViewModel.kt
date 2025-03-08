package com.fatih.prayertime.presentation.hadith_collections_screen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fatih.prayertime.data.local.entity.FavoritesEntity
import com.fatih.prayertime.data.remote.dto.hadithdto.HadithCollection
import com.fatih.prayertime.domain.model.HadithSectionCardData
import com.fatih.prayertime.domain.use_case.hadith_use_cases.GetHadithCollectionsUseCase
import com.fatih.prayertime.domain.use_case.favorite_use_cases.AddFavoriteUseCase
import com.fatih.prayertime.domain.use_case.favorite_use_cases.IsFavoriteUseCase
import com.fatih.prayertime.domain.use_case.favorite_use_cases.RemoveFavoriteUseCase
import com.fatih.prayertime.domain.model.FavoritesEntity
import com.fatih.prayertime.domain.model.FavoritesType
import com.fatih.prayertime.domain.use_case.favorites_use_cases.AddFavoriteUseCase
import com.fatih.prayertime.domain.use_case.favorites_use_cases.IsFavoriteUseCase
import com.fatih.prayertime.domain.use_case.favorites_use_cases.RemoveFavoriteUseCase
import com.fatih.prayertime.util.FavoritesType
import com.fatih.prayertime.util.Resource
import com.fatih.prayertime.util.Status
import com.fatih.prayertime.util.combineSectionsAndDetails
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HadithCollectionViewModel @Inject constructor(
    private val getHadithCollectionsUseCase: GetHadithCollectionsUseCase,
    private val addFavoriteUseCase: AddFavoriteUseCase,
    private val removeFavoriteUseCase: RemoveFavoriteUseCase,
    private val isFavoriteUseCase: IsFavoriteUseCase
) : ViewModel() {

    private val _hadithCollectionPath : MutableStateFlow<String> = MutableStateFlow("")
    val hadithCollectionPath = _hadithCollectionPath

    private val _hadithCollection : MutableStateFlow<Resource<HadithCollection>> = MutableStateFlow(Resource.loading())
    val hadithCollection = _hadithCollection

    private val _hadithSectionCardDataList : MutableStateFlow<Resource<List<HadithSectionCardData>>> = MutableStateFlow(Resource.loading())
    val hadithSectionCardDataList = _hadithSectionCardDataList

    private val _selectedHadithSection : MutableStateFlow<HadithSectionCardData?> = MutableStateFlow(null)
    val selectedHadithSection = _selectedHadithSection

    private val _selectedHadithIndex : MutableStateFlow<Int> = MutableStateFlow(0)
    val selectedHadithIndex = _selectedHadithIndex

    private val _isFavorite = MutableStateFlow(false)
    val isFavorite = _isFavorite.asStateFlow()

    fun getHadithCollection() = viewModelScope.launch(Dispatchers.IO){
        _hadithCollection.emit(Resource.loading())
        _hadithCollection.emit(getHadithCollectionsUseCase(_hadithCollectionPath.value))
    }

    fun updateHadithCollectionPath(collectionPath: String) = viewModelScope.launch(Dispatchers.Default){
        _hadithCollectionPath.emit(collectionPath)
    }

    fun updateSelectedHadithSection(hadithSectionCardData: HadithSectionCardData) = viewModelScope.launch(Dispatchers.Default){
        _selectedHadithSection.emit(hadithSectionCardData)
        _selectedHadithIndex.emit(0)
    }

    fun updateSelectedHadithIndex(hadithIndex : Int) = viewModelScope.launch(Dispatchers.Default){
        _selectedHadithIndex.emit(hadithIndex)
    }

    fun checkIsFavorite(hadithId: Int) = viewModelScope.launch(Dispatchers.IO) {
        _isFavorite.value = isFavoriteUseCase(hadithId, FavoritesType.HADIS.name)
    }

    fun toggleFavorite(hadithId: Int, title: String, content: String) {
        viewModelScope.launch {
            if (_isFavorite.value) {
                removeFavoriteUseCase(
                    FavoritesEntity(
                        itemId = hadithId,
                        type = FavoritesType.HADIS.name,
                        title = title,
                        content = content
                    )
                )
            } else {
                addFavoriteUseCase(
                    FavoritesEntity(
                        itemId = hadithId,
                        type = FavoritesType.HADIS.name,
                        title = title,
                        content = content
                    )
                )
            }
            _isFavorite.value = !_isFavorite.value
        }
    }

    init {
        viewModelScope.launch(Dispatchers.IO){
            _hadithCollectionPath.collectLatest { path ->
                if (path.isNotEmpty()) { // Boş path'leri gereksiz yere işlememek için
                    Log.d("HadithCollectionViewModel", "Collection path: $path")
                    getHadithCollection()
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
                        _hadithSectionCardDataList.emit(Resource.success(combineSectionsAndDetails(hadithCollection.value.data!!)))
                    }
                }
            }
        }
    }

    override fun onCleared() {
        println("oncLEARED")
        super.onCleared()
    }
}