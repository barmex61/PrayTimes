package com.fatih.prayertime.presentation.esmaul_husna_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fatih.prayertime.domain.model.EsmaulHusna
import com.fatih.prayertime.domain.repository.LocalDataRepository
import com.fatih.prayertime.util.model.state.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EsmaulHusnaViewModel @Inject constructor(
    private val localDataRepository: LocalDataRepository
) : ViewModel() {

    private val _esmaulHusnaState = MutableStateFlow<Resource<List<EsmaulHusna>>>(Resource.loading())
    val esmaulHusnaState: StateFlow<Resource<List<EsmaulHusna>>> = _esmaulHusnaState.asStateFlow()

    init {
        loadEsmaulHusna()
    }

    fun loadEsmaulHusna() {
        viewModelScope.launch {
            try {
                localDataRepository.loadEsmaulHusna()
                localDataRepository.getEsmaulHusna().collect { esmaulHusnaList ->
                    _esmaulHusnaState.value = Resource.success(esmaulHusnaList)
                }
            } catch (e: Exception) {
                _esmaulHusnaState.value = Resource.error("Error : ${e.localizedMessage}")
            }
        }
    }
} 