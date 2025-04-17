package com.rahman.pemiluapp.domain.repositories

import com.rahman.pemiluapp.data.model.VoterDataModel
import com.rahman.pemiluapp.utils.Response

interface ShowDataRepository {
    fun getAllVoters(): List<VoterDataModel>
    fun getDataVoter(nik: String?, onResponse: (Response) -> Unit)
    fun deleteDataVoter(nik: String?, onResponse: (Response) -> Unit)
}