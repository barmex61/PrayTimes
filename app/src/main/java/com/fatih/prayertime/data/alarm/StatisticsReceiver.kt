package com.fatih.prayertime.data.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.fatih.prayertime.R
import javax.inject.Inject

class StatisticsReceiver @Inject constructor(
    private val
) : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        when{
            action == context.getString(R.string.yes) ->{}
            action == context.getString(R.string.no) ->{}
            else -> Unit
        }
    }
}