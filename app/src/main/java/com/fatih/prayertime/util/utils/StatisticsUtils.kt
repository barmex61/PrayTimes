package com.fatih.prayertime.util.utils

object StatisticsUtils {
    fun generateStatisticsId(prayType : String,alarmDate : String ) = (prayType + alarmDate).hashCode()

}