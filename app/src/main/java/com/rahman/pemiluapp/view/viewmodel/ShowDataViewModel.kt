package com.rahman.pemiluapp.view.viewmodel

import androidx.lifecycle.viewModelScope
import com.rahman.pemiluapp.data.model.VoterDataModel
import com.rahman.pemiluapp.domain.usecase.DataUsecase
import com.rahman.pemiluapp.domain.util.Response
import kotlinx.coroutines.launch

class ShowDataViewModel(private val repository: DataUsecase) : BaseViewModel<List<VoterDataModel>>() {
    fun getAllVoters() = viewModelScope.launch {
        try {
            setLoading()
            val dataList = repository.getAllVotersData()

            if (dataList.isNotEmpty()) setSuccess(dataList)
            else setFailure()
        } catch (e: Exception) {
            setError(e.localizedMessage)
        }
    }

    fun searchVoter(query: String?) = viewModelScope.launch {
        try {
            setLoading()
            if (!query.isNullOrEmpty()) {
                val dataList = repository.searchVoter(query)

                if (dataList.isNotEmpty()) setSuccess(dataList)
                else setFailure()
            } else {
                getAllVoters()
            }
        } catch (e: Exception) {
            setError(e.localizedMessage)
        }
    }

    fun getDataVoter(nik: String?, onResponse: (Response<VoterDataModel>) -> Unit) = viewModelScope.launch {
        repository.getDataVoter(nik, onResponse)
    }

    fun deleteDataVoter(nik: String?, onResponse: (Response<Nothing>) -> Unit) = viewModelScope.launch {
        try {
            setLoading()

            repository.deleteDataVoter(nik) { response ->
                onResponse(response)

                if (response is Response.Success) getAllVoters()
            }
        } catch (e: Exception) {
            setError(e.localizedMessage)
        }
    }
}