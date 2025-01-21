package com.fatih.prayertime.domain.repository

import com.fatih.prayertime.domain.model.Address
import com.fatih.prayertime.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

interface LocationAndAddressRepository {

    suspend fun getLocationAndAddressInformation() : Flow<Resource<Address>>
    fun removeCallback()
}