package com.fatih.prayertime.presentation.hadith_screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fatih.prayertime.data.local.entity.FavoritesEntity
import com.fatih.prayertime.data.remote.dto.hadithdto.Hadith
import com.fatih.prayertime.domain.model.HadithSectionData
import com.fatih.prayertime.domain.use_case.favorites_use_cases.AddFavoriteUseCase
import com.fatih.prayertime.domain.use_case.favorites_use_cases.IsFavoriteUseCase
import com.fatih.prayertime.domain.use_case.favorites_use_cases.RemoveFavoriteUseCase
import com.fatih.prayertime.domain.use_case.hadith_use_cases.GetHadithCollectionsUseCase
import com.fatih.prayertime.util.extensions.withRetry
import com.fatih.prayertime.util.model.enums.FavoritesType
import com.fatih.prayertime.util.model.state.Resource
import com.fatih.prayertime.util.utils.HadithUtils.combineSectionsAndDetails
import com.fatih.prayertime.util.utils.HadithUtils.generateFavoriteItemId
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HadithViewModel @Inject constructor(
    private val getHadithCollectionsUseCase: GetHadithCollectionsUseCase,
    private val isFavoriteUseCase: IsFavoriteUseCase,
    private val removeFavoriteUseCase: RemoveFavoriteUseCase,
    private val addFavoriteUseCase: AddFavoriteUseCase,
)  : ViewModel(){


    private val hadithCollectionPath : MutableStateFlow<String> = MutableStateFlow("")
    private val hadithSectionRetryTrigger = MutableSharedFlow<Unit>()
    private val favoriteRetryTrigger = MutableSharedFlow<Unit>()

    @OptIn(ExperimentalCoroutinesApi::class)
    private val hadithCollection = hadithCollectionPath.filter { it.isNotEmpty() }.flatMapLatest { it->
        getHadithCollectionsUseCase(it)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Resource.loading())

    private val _hadithSectionDataList = hadithCollection.withRetry(hadithSectionRetryTrigger).map {
        combineSectionsAndDetails(it)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Resource.loading())
    val hadithSectionCardDataList = _hadithSectionDataList

    private val _selectedHadithSection : MutableStateFlow<HadithSectionData?> = MutableStateFlow(null)
    val selectedHadithSection = _selectedHadithSection

    private val selectedHadithSectionIndex = MutableStateFlow(0)

    private val _selectedHadithIndex : MutableStateFlow<Int> = MutableStateFlow(0)
    val selectedHadithIndex = _selectedHadithIndex

    private val _selectedHadith: StateFlow<Hadith?> = combine(
        _selectedHadithIndex,
        _selectedHadithSection
    ) { index, section ->
        section?.hadithList?.getOrNull(index)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    val selectedHadith = _selectedHadith

    private val _isFavorite = _selectedHadith.withRetry(favoriteRetryTrigger).map {
        val id = generateFavoriteItemId(
            FavoritesType.HADIS.name,
            selectedHadith.value!!.text,
            hadithCollectionPath.value,
            selectedHadithSectionIndex.value,
            selectedHadithIndex.value
        )
        isFavoriteUseCase(id, FavoritesType.HADIS.name)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000),false)
    val isFavorite = _isFavorite


    fun updateHadithCollectionPath(collectionPath: String) = viewModelScope.launch(Dispatchers.Default){
        hadithCollectionPath.emit(collectionPath)
    }

    fun updateSelectedHadithSection(hadithSectionData: HadithSectionData, index:Int) = viewModelScope.launch(Dispatchers.Default){
        _selectedHadithSection.emit(hadithSectionData)
        selectedHadithSectionIndex.emit(index)
        _selectedHadithIndex.emit(0)
    }

    private fun updateSelectedHadithSection(index:Int) = viewModelScope.launch(Dispatchers.Default){
        _selectedHadithSection.emit(hadithSectionCardDataList.value.data?.get(index))
    }

    fun updateSelectedHadithSectionIndex(index: Int) = viewModelScope.launch(Dispatchers.Default){
        selectedHadithSectionIndex.emit(index)
    }

    fun updateSelectedHadithIndex(hadithIndex : Int) = viewModelScope.launch(Dispatchers.Default){
        _selectedHadithIndex.emit(hadithIndex)
    }


    fun toggleFavorite(hadith: Hadith) = viewModelScope.launch(Dispatchers.IO){
        val id = generateFavoriteItemId(FavoritesType.HADIS.name, hadith.text, hadithCollectionPath.value, selectedHadithSectionIndex.value, selectedHadithIndex.value)
        if (_isFavorite.value) {
            removeFavoriteUseCase(
                FavoritesEntity(
                    type = FavoritesType.HADIS.name,
                    title = hadith.text,
                    itemId = id,
                    hadithId = selectedHadithIndex.value,
                    content = "",
                    hadithCollectionPath = hadithCollectionPath.value,
                    hadithSectionIndex = selectedHadithSectionIndex.value,
                    hadithIndex = selectedHadithIndex.value
                )
            )
        } else {
            addFavoriteUseCase(
                FavoritesEntity(
                    type = FavoritesType.HADIS.name,
                    title = hadith.text,
                    itemId = id,
                    content ="",
                    hadithId = selectedHadithIndex.value,
                    hadithCollectionPath = hadithCollectionPath.value,
                    hadithSectionIndex = selectedHadithSectionIndex.value,
                    hadithIndex = selectedHadithIndex.value
                )
            )
        }
        favoriteRetryTrigger.emit(Unit)
    }


}