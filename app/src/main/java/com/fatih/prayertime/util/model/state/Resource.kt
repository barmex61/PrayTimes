package com.fatih.prayertime.util.model.state

data class Resource<out T>(
    val status: Status,
    val data: T?,
    val message: String?,
    val progress: Int = 0,
    val downloadedSize: Long = 0L,
    val totalSize: Long = 0L
) {
    companion object {
        fun <T> success(data: T?): Resource<T> {
            return Resource(Status.SUCCESS, data, null)
        }

        fun <T> error(message: String?): Resource<T> {
            return Resource(Status.ERROR, null, message)
        }

        fun <T> loading(progress: Int = 0, downloadedSize: Long = 0L, totalSize: Long = 0L): Resource<T> {
            return Resource(Status.LOADING, null, null, progress, downloadedSize, totalSize)
        }
    }
}

enum class Status {
    LOADING,
    SUCCESS,
    ERROR
} 