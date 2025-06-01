package com.rahman.pemiluapp.data.repository

import android.util.Log
import com.rahman.pemiluapp.data.model.VoterDataModel
import com.rahman.pemiluapp.data.sql.DatabaseHelper
import com.rahman.pemiluapp.domain.repositories.ShowDataRepository
import com.rahman.pemiluapp.utils.DateFormatter.tryFormatTimestamp
import com.rahman.pemiluapp.domain.util.Response
import com.rahman.pemiluapp.domain.util.VoterDataProcessor
import java.io.File

class ShowDataRepositoryImpl(private val sqlDatabase: DatabaseHelper) : ShowDataRepository {
    override suspend fun getAllVoters(): List<VoterDataModel> {
        val rawVoters = sqlDatabase.getAllVoter()
        return VoterDataProcessor.processVoters(rawVoters)
    }

    override suspend fun searchVoter(query: String): List<VoterDataModel> {
        val rawVoters = sqlDatabase.searchVoter(query)
        return VoterDataProcessor.processVoters(rawVoters)
    }

    override suspend fun getDataVoter(nik: String?, onResponse: (Response<VoterDataModel>) -> Unit) {
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

    override suspend fun deleteDataVoter(nik: String?, onResponse: (Response<Nothing>) -> Unit) {
        try {
            onResponse(Response.Loading)

            if (nik.isNullOrEmpty()) {
                onResponse(Response.Failure())
                return
            }

            val dataVoter = sqlDatabase.getVoterByID(nik)
            val deleteData = sqlDatabase.deleteVoterByID(nik)

            if (deleteData != -1) {
                dataVoter?.gambar?.let { imagePath ->
                    val imageFile = File(imagePath)

                    val successDeleteFile = try {
                        if (imageFile.exists()) imageFile.delete() else false
                    } catch (_: Exception) {
                        false
                    }
                    if (!successDeleteFile) {
                        Log.e("deleteFile", "Failed to delete image file")
                        //todo: file not deleted
                    }
                }
                onResponse(Response.Success())
            } else onResponse(Response.Failure())
        } catch (e: Exception) {
            onResponse(Response.Error(e.localizedMessage))
        }
    }
}