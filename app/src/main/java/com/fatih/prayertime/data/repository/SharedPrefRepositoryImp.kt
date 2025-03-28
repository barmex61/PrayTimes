package com.fatih.prayertime.data.repository

import com.fatih.prayertime.data.settings.SharedPreferencesManager
import com.fatih.prayertime.domain.repository.SharedPrefRepository
import javax.inject.Inject


class SharedPrefRepositoryImp @Inject constructor(
    private val sharedPreferencesManager: SharedPreferencesManager
) : SharedPrefRepository{

    override fun getStatisticKey(): Boolean {
        return sharedPreferencesManager.getStatisticKey()
    }

    override fun insertStatisticKey() {
        sharedPreferencesManager.insertStatisticKey()
    }

    override fun clearStatisticKey() {
        sharedPreferencesManager.clearStatisticKey()
    }
}