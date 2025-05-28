package com.rahman.pemiluapp.data.repository

import com.rahman.pemiluapp.data.model.VoterDataModel
import com.rahman.pemiluapp.data.sql.DatabaseHelper
import com.rahman.pemiluapp.domain.repositories.ShowDataRepository
import com.rahman.pemiluapp.utils.DateFormatter.tryFormatTimestamp
import com.rahman.pemiluapp.domain.util.Response
import com.rahman.pemiluapp.domain.util.VoterDataProcessor

class ShowDataRepositoryImpl(private val sqlDatabase: DatabaseHelper) : ShowDataRepository {
    override fun getAllVoters(): List<VoterDataModel> {
        val rawVoters = sqlDatabase.getAllVoter()
        return VoterDataProcessor.processVoters(rawVoters)
    }

    override fun searchVoter(query: String): List<VoterDataModel> {
        val rawVoters = sqlDatabase.searchVoter(query)
        return VoterDataProcessor.processVoters(rawVoters)
    }

    override fun getDataVoter(nik: String?, onResponse: (Response<VoterDataModel>) -> Unit) {
        try {
            onResponse(Response.Loading)

            if (nik.isNullOrEmpty()) {
                onResponse(Response.Failure())
                return
            }

            val dataVoter = sqlDatabase.getVoterByID(nik)

            if (dataVoter != null) {
                val formattedVoter = dataVoter.copy(tanggal = tryFormatTimestamp(dataVoter.tanggal))
                onResponse(Response.Success(formattedVoter))
            } else onResponse(Response.Failure())

        } catch (e: Exception) {
            onResponse(Response.Error(e.localizedMessage))
        }
    }

    override fun deleteDataVoter(nik: String?, onResponse: (Response<Nothing>) -> Unit) {
        try {
            onResponse(Response.Loading)

            if (nik.isNullOrEmpty()) {
                onResponse(Response.Failure())
                return
            }

            val deleteData = sqlDatabase.deleteVoterByID(nik)

            if (deleteData != -1) onResponse(Response.Success())
            else onResponse(Response.Failure())
        } catch (e: Exception) {
            onResponse(Response.Error(e.localizedMessage))
        }
    }
}