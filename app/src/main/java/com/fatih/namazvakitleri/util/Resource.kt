package com.fatih.namazvakitleri.util

data class Resource<out T>(val data : T?,val message : String?,val status: Status){
    companion object {
        fun <T> success(data: T?): Resource<T> = Resource(data, null, Status.SUCCESS)
        fun <T> error(message: String?): Resource<T> = Resource(null, message, Status.ERROR)
        fun <T> loading(): Resource<T> = Resource(null, null, Status.LOADING)
    }
}

enum class Status{
    LOADING,
    SUCCESS,
    ERROR
}