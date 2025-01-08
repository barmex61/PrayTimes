package com.fatih.namazvakitleri.domain.use_case.get_address_from_database

import com.fatih.namazvakitleri.data.local.dao.AddressDao
import javax.inject.Inject

class GetAddressFromDatabaseUseCase @Inject constructor(
    private val addressDao: AddressDao
) {
    suspend operator fun invoke() = addressDao.getCurrentAddress()
}