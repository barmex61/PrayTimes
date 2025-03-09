package com.fatih.prayertime.data.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.fatih.prayertime.R
import com.fatih.prayertime.data.local.entity.PrayerStatisticsEntity
import com.fatih.prayertime.domain.use_case.statistics_use_cases.InsertPlayerStatisticsUseCase
import com.fatih.prayertime.domain.use_case.statistics_use_cases.IsStatisticsExistsUseCase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class StatisticsReceiver : BroadcastReceiver() {

    @Inject
    lateinit var insertPlayerStatisticsUseCase: InsertPlayerStatisticsUseCase
    @Inject
    lateinit var isStatisticsExistsUseCase: IsStatisticsExistsUseCase
    @Inject
    lateinit var updateStatisticsUseCase: InsertPlayerStatisticsUseCase

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        val prayType = intent.getStringExtra("PRAY_TYPE") ?: ""
        val prayDate = intent.getStringExtra("PRAY_DATE") ?: ""
        val id = (prayType + prayDate).hashCode()
        val statisticsEntity = when (action) {
            context.getString(R.string.yes) -> {
                PrayerStatisticsEntity(id = id,prayerType = prayType, date = prayDate, isCompleted = true)
            }
            context.getString(R.string.no) -> {
                PrayerStatisticsEntity(id = id,prayerType = prayType, date = prayDate, isCompleted = false)
            }
            else -> return
        }
        CoroutineScope(Dispatchers.IO).launch {
            if (isStatisticsExistsUseCase(prayType, prayDate)) {
                updateStatisticsUseCase(statisticsEntity)
            }else{
                insertPlayerStatisticsUseCase(statisticsEntity)
            }

        }

    }
}