package com.rahman.pemiluapp.domain.usecase

import com.rahman.pemiluapp.data.model.CoordinateModel
import com.rahman.pemiluapp.data.model.VoterDataModel
import com.rahman.pemiluapp.domain.util.Response
import java.io.File

interface DataUsecase {
    fun getCurrentLocation(coordinate: CoordinateModel, onResponse: (Response<String>) -> Unit)
    fun validateDataExists(nik: String, onResponse: (Response<String>) -> Unit)
    fun addVoterData(voterData: VoterDataModel, onResponse: (Response<VoterDataModel>) -> Unit)

    fun getAllVotersData(): List<VoterDataModel>
    fun searchVoter(query: String): List<VoterDataModel>
    fun getDataVoter(nik: String?, onResponse: (Response<VoterDataModel>) -> Unit)
    fun deleteDataVoter(nik: String?, onResponse: (Response<Nothing>) -> Unit)

    suspend fun exportData(file: File, onResponse: (Response<Boolean>) -> Unit)
    suspend fun importData(file: File, onResponse: (Response<Boolean>) -> Unit)
}