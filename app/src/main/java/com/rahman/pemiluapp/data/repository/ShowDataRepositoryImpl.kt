package com.rahman.pemiluapp.data.repository

import com.rahman.pemiluapp.data.model.VoterDataModel
import com.rahman.pemiluapp.data.sql.DatabaseHelper
import com.rahman.pemiluapp.domain.repositories.ShowDataRepository
import com.rahman.pemiluapp.utils.DateFormatter.tryFormatTimestamp
import com.rahman.pemiluapp.utils.Response

class ShowDataRepositoryImpl(private val sqlDatabase: DatabaseHelper) : ShowDataRepository {
    override fun getAllVoters(): List<VoterDataModel> {
        return sqlDatabase.getAllVoter()
            .sortedByDescending { it.tanggal?.takeIf { date -> date.isNotEmpty() } ?: it.nama }
            .map { it.copy(tanggal = tryFormatTimestamp(it.tanggal)) }
    }

    override fun getDataVoter(nik: String?, onResponse: (Response) -> Unit) {
        try {
            when {
                nik.isNullOrEmpty() -> onResponse(Response.Failure())
                else -> {
                    val dataVoter = sqlDatabase.getVoterByID(nik)

                    if (dataVoter != null) onResponse(Response.Success(dataVoter))
                    else onResponse(Response.Failure())
                }
            }
        } catch (e: Exception) {
            onResponse(Response.Error(e.localizedMessage))
        }
    }

    override fun deleteDataVoter(nik: String?, onResponse: (Response) -> Unit) {
        try {
            when {
                nik.isNullOrEmpty() -> onResponse(Response.Failure())
                else -> {
                    val deleteData = sqlDatabase.deleteVoterByID(nik)

                    if (deleteData != -1) onResponse(Response.Success())
                    else onResponse(Response.Failure())
                }
            }
        } catch (e: Exception) {
            onResponse(Response.Error(e.localizedMessage))
        }
    }
}