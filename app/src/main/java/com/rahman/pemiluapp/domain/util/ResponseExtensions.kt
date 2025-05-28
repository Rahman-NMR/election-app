package com.rahman.pemiluapp.domain.util

inline fun <T> Response<T>.onSuccess(action: (data: T?) -> Unit): Response<T> {
    if (this is Response.Success) action(data)
    return this
}

inline fun <T> Response<T>.onFailure(action: (String?) -> Unit): Response<T> {
    if (this is Response.Failure) action(message)
    return this
}

inline fun <T> Response<T>.onError(action: (String?) -> Unit): Response<T> {
    if (this is Response.Error) action(message)
    return this
}

inline fun <T> Response<T>.onLoading(action: () -> Unit): Response<T> {
    if (this is Response.Loading) action()
    return this
}