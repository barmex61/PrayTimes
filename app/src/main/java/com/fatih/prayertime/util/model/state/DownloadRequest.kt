package com.fatih.prayertime.util.model.state

data class DownloadRequest(
    val audioPath: String,
    val bitrate: Int,
    val reciter: String,
    val audioNumber: Int,
    val shouldCache: Boolean
)
