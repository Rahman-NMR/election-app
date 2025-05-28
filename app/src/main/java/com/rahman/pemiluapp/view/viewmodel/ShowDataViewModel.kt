package com.rahman.pemiluapp.view.viewmodel

import com.rahman.pemiluapp.data.model.VoterDataModel
import com.rahman.pemiluapp.domain.usecase.DataUsecase
import com.rahman.pemiluapp.domain.util.Response

class ShowDataViewModel(private val repository: DataUsecase) : BaseViewModel<List<VoterDataModel>>() {
    fun getAllVoters() {
        try {
            setLoading()
            val dataList = repository.getAllVotersData()

            if (dataList.isNotEmpty()) setSuccess(dataList)
            else setFailure()
        } catch (e: Exception) {
            setError(e.localizedMessage)
        }
    }

    fun searchVoter(query: String?) {
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

    fun getDataVoter(nik: String?, onResponse: (Response<VoterDataModel>) -> Unit) {
        repository.getDataVoter(nik, onResponse)
    }

    fun deleteDataVoter(nik: String?, onResponse: (Response<Nothing>) -> Unit) {
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