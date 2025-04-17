package com.rahman.pemiluapp.domain.repositories

import com.rahman.pemiluapp.data.model.CoordinateModel
import com.rahman.pemiluapp.data.model.VoterDataModel
import com.rahman.pemiluapp.utils.Response

interface EntryDataRepository {
    fun getCurrentLocation(coordinate: CoordinateModel, onLocationResult: (Response) -> Unit)
    fun validateDataExists(nik: String, onResponse: (Response) -> Unit)
    fun addVoterData(voterData: VoterDataModel, onResponse: (Response) -> Unit)
}