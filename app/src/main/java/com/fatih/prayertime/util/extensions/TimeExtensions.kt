package com.fatih.prayertime.util.extensions


fun String?.convertTimeToSeconds(): Int {
    this?.let { timeString ->
        val timeParts = timeString.split(":")
        if (timeParts.size in 2..3) {
            try {
                val hours = timeParts[0].toInt()
                val minutes = timeParts[1].toInt()
                val seconds = if (timeParts.size == 3) timeParts[2].toInt() else 0
                return hours * 3600 + minutes * 60 + seconds
            } catch (e: NumberFormatException) {
                return 0
            }
        }
    }
    return 0
}

fun Long.addMinutesToLong(minutes : Long) = this + (minutes * 60 * 1000)