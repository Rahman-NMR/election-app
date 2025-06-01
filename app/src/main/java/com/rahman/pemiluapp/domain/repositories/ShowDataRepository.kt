package com.rahman.pemiluapp.domain.repositories

import com.rahman.pemiluapp.data.model.VoterDataModel
import com.rahman.pemiluapp.domain.util.Response

interface ShowDataRepository {
    suspend fun getAllVoters(): List<VoterDataModel>
    suspend fun searchVoter(query: String): List<VoterDataModel>
    suspend fun getDataVoter(nik: String?, onResponse: (Response<VoterDataModel>) -> Unit)
    suspend fun deleteDataVoter(nik: String?, onResponse: (Response<Nothing>) -> Unit)
}