package com.rahman.pemiluapp.domain.util

sealed class Response<out T> {
    data class Success<T>(val data: T? = null) : Response<T>()
    data class Failure(val message: String? = null) : Response<Nothing>()
    data class Error(val message: String? = null) : Response<Nothing>()
    object Loading : Response<Nothing>()
}