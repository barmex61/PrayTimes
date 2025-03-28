package com.fatih.prayertime.domain.repository

interface SharedPrefRepository {
    fun insertStatisticKey()
    fun getStatisticKey() : Boolean
    fun clearStatisticKey()
}