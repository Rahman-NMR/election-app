package com.rahman.pemiluapp.domain.repositories

import com.rahman.pemiluapp.data.model.CoordinateModel
import com.rahman.pemiluapp.data.model.VoterDataModel
import com.rahman.pemiluapp.domain.util.Response

interface EntryDataRepository {
    fun getCurrentLocation(coordinate: CoordinateModel, onLocationResult: (Response<String>) -> Unit)
    fun validateDataExists(nik: String, onResponse: (Response<String>) -> Unit)
    fun addVoterData(voterData: VoterDataModel, onResponse: (Response<VoterDataModel>) -> Unit)
}