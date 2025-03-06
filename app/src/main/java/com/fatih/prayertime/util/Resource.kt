package com.fatih.prayertime.util

data class Resource<out T>(val data : T?, var message : String?, val status: Status, val exception: Exception? = null){
    companion object {
        fun <T> success(data: T?): Resource<T> = Resource(data, null, Status.SUCCESS)
        fun <T> error(message: String?,exception: Exception? = null): Resource<T> = Resource(null, message, Status.ERROR)
        fun <T> loading(): Resource<T> = Resource(null, null, Status.LOADING)
    }
}

enum class Status{
    LOADING,
    SUCCESS,
    ERROR
}