package com.fatih.prayertime.util

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class PairConverter {
    @TypeConverter
    fun fromPair(pair: Pair<String, String>): String {
        val gson = Gson()
        val type = object : TypeToken<Pair<String, String>>() {}.type
        return gson.toJson(pair, type)
    }

    @TypeConverter
    fun toPair(json: String?): Pair<String, String> {
        val gson = Gson()
        val type = object : TypeToken<Pair<String, String>>() {}.type
        return gson.fromJson(json, type)
    }
}

class AlarmPairConverter {
    @TypeConverter
    fun fromPair(pair: Pair<Boolean, Long?>): String {
        return Gson().toJson(pair)
    }

    @TypeConverter
    fun toPair(json: String): Pair<Boolean, Long?> {
        val type = object : TypeToken<Pair<Boolean, Long?>>() {}.type
        return Gson().fromJson(json, type)
    }
}