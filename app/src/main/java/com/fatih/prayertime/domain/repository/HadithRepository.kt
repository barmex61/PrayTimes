package com.fatih.prayertime.domain.repository

import com.fatih.prayertime.data.remote.dto.hadithdto.HadithEdition
import com.fatih.prayertime.util.Resource

interface HadithRepository {

    suspend fun getHadithEditions() : Resource<HadithEdition>
}