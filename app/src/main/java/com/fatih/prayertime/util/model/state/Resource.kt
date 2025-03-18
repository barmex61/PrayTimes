package com.fatih.prayertime.util.model.state

data class Resource<out T>(
    val data: T?,
    var message: String?,
    val status: Status,
    val progress: Int = 0,
) {
    companion object {
        fun <T> success(data: T?): Resource<T> = Resource(data, null, Status.SUCCESS)
        fun <T> error(message: String?): Resource<T> = Resource(null, message, Status.ERROR)
        fun <T> loading(progress : Int = 0): Resource<T> = Resource(null, null, Status.LOADING, progress = progress)
    }
}

enum class Status {
    LOADING,
    SUCCESS,
    ERROR
} 