package com.rahman.pemiluapp.domain.repositories

import com.rahman.pemiluapp.data.model.VoterDataModel
import com.rahman.pemiluapp.domain.util.Response

interface ShowDataRepository {
    fun getAllVoters(): List<VoterDataModel>
    fun searchVoter(query: String): List<VoterDataModel>
    fun getDataVoter(nik: String?, onResponse: (Response<VoterDataModel>) -> Unit)
    fun deleteDataVoter(nik: String?, onResponse: (Response<Nothing>) -> Unit)
}