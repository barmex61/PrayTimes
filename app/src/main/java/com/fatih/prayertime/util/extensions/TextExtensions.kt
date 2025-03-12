package com.fatih.prayertime.util.extensions


fun String.capitalizeFirstLetter(): String {
    return this.replaceFirstChar {
        if (it.isLowerCase()) it.titlecase() else it.toString()
    }
}
fun String.toDateInt(): Int {
    val parts = this.split("-")
    if (parts.size == 2) {
        val day = parts[0].toIntOrNull() ?: 0
        val month = parts[1].toIntOrNull() ?: 0
        return day + month * 100
    }
    return 0
}

fun Int.toDateString(): String {
    val day = this % 100
    val month = this / 100
    return String.format("%02d-%02d", day, month)
}