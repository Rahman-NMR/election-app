package com.rahman.pemiluapp.view.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.rahman.pemiluapp.domain.util.Response

abstract class BaseViewModel<T> : ViewModel() {
    protected val liveData = MutableLiveData<Response<T>>(Response.Loading)
    val responseLiveData: LiveData<Response<T>> = liveData

    protected fun setLoading() {
        liveData.value = Response.Loading
    }

    protected fun setSuccess(data: T) {
        liveData.value = Response.Success(data)
    }

    protected fun setFailure(message: String? = "") {
        liveData.value = Response.Failure(message)
    }

    protected fun setError(message: String? = "") {
        liveData.value = Response.Error(message)
    }
}