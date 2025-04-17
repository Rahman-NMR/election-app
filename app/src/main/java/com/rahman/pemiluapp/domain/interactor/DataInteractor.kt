package com.rahman.pemiluapp.domain.interactor

import com.rahman.pemiluapp.data.model.CoordinateModel
import com.rahman.pemiluapp.data.model.VoterDataModel
import com.rahman.pemiluapp.domain.repositories.EntryDataRepository
import com.rahman.pemiluapp.domain.repositories.ShowDataRepository
import com.rahman.pemiluapp.domain.usecase.DataUsecase
import com.rahman.pemiluapp.utils.Response

class DataInteractor(
    private val entryDataRepository: EntryDataRepository,
    private val showDataRepository: ShowDataRepository
) : DataUsecase {

    // enter data
    override fun getCurrentLocation(coordinate: CoordinateModel, onResponse: (Response) -> Unit) {
        return entryDataRepository.getCurrentLocation(coordinate, onResponse)
    }

    override fun validateDataExists(nik: String, onResponse: (Response) -> Unit) {
        return entryDataRepository.validateDataExists(nik, onResponse)
    }

    override fun addVoterData(voterData: VoterDataModel, onResponse: (Response) -> Unit) {
        return entryDataRepository.addVoterData(voterData, onResponse)
    }

    // show data
    override fun getAllVotersData(): List<VoterDataModel> {
        return showDataRepository.getAllVoters()
    }

    override fun getDataVoter(nik: String?, onResponse: (Response) -> Unit) {
        return showDataRepository.getDataVoter(nik, onResponse)
    }

    override fun deleteDataVoter(nik: String?, onResponse: (Response) -> Unit) {
        return showDataRepository.deleteDataVoter(nik, onResponse)
    }
}