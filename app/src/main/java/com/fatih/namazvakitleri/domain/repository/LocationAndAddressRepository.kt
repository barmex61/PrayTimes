package com.fatih.namazvakitleri.domain.repository

import com.fatih.namazvakitleri.domain.model.Address
import com.fatih.namazvakitleri.util.Resource
import kotlinx.coroutines.flow.Flow

interface LocationAndAddressRepository {

    suspend fun getLocationAndAddressInformation() : Flow<Resource<Address>>
    suspend fun getCurrentAddress() : Address?
}