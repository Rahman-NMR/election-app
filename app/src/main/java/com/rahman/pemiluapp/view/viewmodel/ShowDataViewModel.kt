package com.rahman.pemiluapp.view.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.rahman.pemiluapp.data.model.VoterDataModel
import com.rahman.pemiluapp.domain.usecase.DataUsecase
import com.rahman.pemiluapp.utils.Response

class ShowDataViewModel(private val repository: DataUsecase) : ViewModel() {
    private val _votersData = MutableLiveData<List<VoterDataModel>>()
    val votersData: MutableLiveData<List<VoterDataModel>> get() = _votersData

    fun getAllVoters(onResponse: (Response) -> Unit) {
        try {
            val dataList = repository.getAllVotersData()

            if (dataList.isNotEmpty()) {
                _votersData.value = dataList
                onResponse(Response.Success())
            } else {
                _votersData.value = emptyList()
                onResponse(Response.Failure())
            }
        } catch (e: Exception) {
            onResponse(Response.Error(e.localizedMessage))
        }
    }

    fun getDataVoter(nik: String?, onResponse: (Response) -> Unit) {
        repository.getDataVoter(nik, onResponse)
    }

    fun deleteDataVoter(nik: String?, onResponse: (Response) -> Unit) {
        repository.deleteDataVoter(nik) { response ->
            if (response is Response.Success) {
                _votersData.value = _votersData.value?.filter { it.nik != nik }
            }

            onResponse(response)
        }
    }
}