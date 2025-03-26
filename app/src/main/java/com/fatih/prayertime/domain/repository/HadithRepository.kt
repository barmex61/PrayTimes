package com.fatih.prayertime.domain.repository

import com.fatih.prayertime.data.remote.dto.hadithdto.HadithCollection
import com.fatih.prayertime.data.remote.dto.hadithdto.HadithEdition
import com.fatih.prayertime.util.model.state.Resource
import kotlinx.coroutines.flow.Flow

interface HadithRepository {

    fun getHadithEditions() : Flow<Resource<HadithEdition>>
    fun getHadithCollections(collectionPath : String) : Flow<Resource<HadithCollection>>
}