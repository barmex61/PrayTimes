package com.fatih.prayertime.domain.repository

import com.fatih.prayertime.data.remote.dto.hadithdto.HadithCollection
import com.fatih.prayertime.data.remote.dto.hadithdto.HadithEdition
import com.fatih.prayertime.util.model.state.Resource

interface HadithRepository {

    suspend fun getHadithEditions() : Resource<HadithEdition>
    suspend fun getHadithCollections(collectionPath : String) : Resource<HadithCollection>
}