package com.rahman.pemiluapp.domain.usecase

import com.rahman.pemiluapp.data.model.CoordinateModel
import com.rahman.pemiluapp.data.model.VoterDataModel
import com.rahman.pemiluapp.utils.Response

interface DataUsecase {
    fun getCurrentLocation(coordinate: CoordinateModel, onResponse: (Response) -> Unit)
    fun validateDataExists(nik: String, onResponse: (Response) -> Unit)
    fun addVoterData(voterData: VoterDataModel, onResponse: (Response) -> Unit)

    fun getAllVotersData(): List<VoterDataModel>
    fun getDataVoter(nik: String?, onResponse: (Response) -> Unit)
    fun deleteDataVoter(nik: String?, onResponse: (Response) -> Unit)
}