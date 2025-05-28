package com.rahman.pemiluapp.domain.interactor

import com.rahman.pemiluapp.data.model.CoordinateModel
import com.rahman.pemiluapp.data.model.VoterDataModel
import com.rahman.pemiluapp.domain.repositories.EntryDataRepository
import com.rahman.pemiluapp.domain.repositories.FileExportRepository
import com.rahman.pemiluapp.domain.repositories.ShowDataRepository
import com.rahman.pemiluapp.domain.usecase.DataUsecase
import com.rahman.pemiluapp.domain.util.Response
import java.io.File

class DataInteractor(
    private val entryDataRepository: EntryDataRepository,
    private val showDataRepository: ShowDataRepository,
    private val fileExportRepository: FileExportRepository
) : DataUsecase {

    // enter data
    override fun getCurrentLocation(coordinate: CoordinateModel, onResponse: (Response<String>) -> Unit) {
        return entryDataRepository.getCurrentLocation(coordinate, onResponse)
    }

    override fun validateDataExists(nik: String, onResponse: (Response<String>) -> Unit) {
        return entryDataRepository.validateDataExists(nik, onResponse)
    }

    override fun addVoterData(voterData: VoterDataModel, onResponse: (Response<VoterDataModel>) -> Unit) {
        return entryDataRepository.addVoterData(voterData, onResponse)
    }

    // show data
    override fun getAllVotersData(): List<VoterDataModel> {
        return showDataRepository.getAllVoters()
    }

    override fun searchVoter(query: String): List<VoterDataModel> {
        return showDataRepository.searchVoter(query)
    }

    override fun getDataVoter(nik: String?, onResponse: (Response<VoterDataModel>) -> Unit) {
        return showDataRepository.getDataVoter(nik, onResponse)
    }

    override fun deleteDataVoter(nik: String?, onResponse: (Response<Nothing>) -> Unit) {
        return showDataRepository.deleteDataVoter(nik, onResponse)
    }

    // export data
    override suspend fun exportData(file: File, onResponse: (Response<Boolean>) -> Unit) {
        return fileExportRepository.exportToJson(file, onResponse)
    }

    override suspend fun importData(file: File, onResponse: (Response<Boolean>) -> Unit) {
        return fileExportRepository.importFromJson(file, onResponse)
    }
}