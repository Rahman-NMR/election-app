package com.rahman.pemiluapp.utils

sealed class Response {
    data class Success(val data: Any? = null) : Response()
    data class Failure(val message: String? = null) : Response()
    data class Error(val message: String? = null) : Response()
    object Loading : Response()
}