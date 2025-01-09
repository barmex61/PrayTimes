package com.fatih.prayertime.presentation.main_activity.viewmodel

import androidx.lifecycle.ViewModel
import com.fatih.prayertime.domain.use_case.get_location_and_adress_use_case.GetLocationAndAddressUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val getLocationAndAddressUseCase: GetLocationAndAddressUseCase,
) : ViewModel() {



}