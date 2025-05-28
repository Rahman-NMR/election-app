package com.rahman.pemiluapp.data.repository

import android.location.Geocoder
import com.rahman.pemiluapp.data.model.CoordinateModel
import com.rahman.pemiluapp.data.model.VoterDataModel
import com.rahman.pemiluapp.data.sql.DatabaseHelper
import com.rahman.pemiluapp.domain.repositories.EntryDataRepository
import com.rahman.pemiluapp.domain.util.Response

class EntryDataRepositoryImpl(
    private val dbHelper: DatabaseHelper,
    private val geoCoder: Geocoder
) : EntryDataRepository {
    override fun getCurrentLocation(coordinate: CoordinateModel, onLocationResult: (Response<String>) -> Unit) {
        try {
            onLocationResult(Response.Loading)

            val address = geoCoder.getFromLocation(coordinate.lat, coordinate.lng, 10)

            if (!address.isNullOrEmpty()) {
                val fullAddress = address[0].getAddressLine(0)
                onLocationResult(Response.Success(fullAddress))
            } else onLocationResult(Response.Failure())
        } catch (e: Exception) {
            onLocationResult(Response.Error(e.localizedMessage))
        }
    }

    override fun validateDataExists(nik: String, onResponse: (Response<String>) -> Unit) {
        try {
            onResponse(Response.Loading)

            val voterData = dbHelper.getVoterByID(nik)

            if (voterData != null) {
                val voterNik = voterData.nik
                onResponse(Response.Success(voterNik))
            } else onResponse(Response.Failure())
        } catch (e: Exception) {
            onResponse(Response.Error(e.localizedMessage))
        }
    }

    override fun addVoterData(voterData: VoterDataModel, onResponse: (Response<VoterDataModel>) -> Unit) {
        try {
            onResponse(Response.Loading)

            val addData = dbHelper.insertVoter(voterData)

            if (addData != -1L) onResponse(Response.Success())
            else onResponse(Response.Failure())
        } catch (e: Exception) {
            onResponse(Response.Error(e.localizedMessage))
        }
    }
}