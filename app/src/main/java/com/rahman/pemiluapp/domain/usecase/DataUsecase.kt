package com.rahman.pemiluapp.domain.usecase

import android.net.Uri
import com.rahman.pemiluapp.data.model.CoordinateModel
import com.rahman.pemiluapp.data.model.VoterDataModel
import com.rahman.pemiluapp.domain.util.Response
import java.io.File

interface DataUsecase {
    suspend fun getCurrentLocation(coordinate: CoordinateModel, onResponse: (Response<String>) -> Unit)
    suspend fun validateDataExists(nik: String, onResponse: (Response<String>) -> Unit)
    suspend fun imageProcessing(uri: Uri?): Uri?
    suspend fun addVoterData(voterData: VoterDataModel, onResponse: (Response<VoterDataModel>) -> Unit)

    suspend fun getAllVotersData(): List<VoterDataModel>
    suspend fun searchVoter(query: String): List<VoterDataModel>
    suspend fun getDataVoter(nik: String?, onResponse: (Response<VoterDataModel>) -> Unit)
    suspend fun deleteDataVoter(nik: String?, onResponse: (Response<Nothing>) -> Unit)

    suspend fun exportData(file: File, onResponse: (Response<Boolean>) -> Unit)
    suspend fun importData(file: File, onResponse: (Response<Boolean>) -> Unit)
}