package com.rahman.pemiluapp.domain.repositories

import android.net.Uri
import com.rahman.pemiluapp.data.model.CoordinateModel
import com.rahman.pemiluapp.data.model.VoterDataModel
import com.rahman.pemiluapp.domain.util.Response

interface EntryDataRepository {
    suspend fun getCurrentLocation(coordinate: CoordinateModel, onLocationResult: (Response<String>) -> Unit)
    suspend fun validateDataExists(nik: String, onResponse: (Response<String>) -> Unit)
    suspend fun imageProcessing(uri: Uri?): Uri?
    suspend fun addVoterData(voterData: VoterDataModel, onResponse: (Response<VoterDataModel>) -> Unit)
}