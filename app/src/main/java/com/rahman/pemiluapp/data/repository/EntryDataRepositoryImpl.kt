package com.rahman.pemiluapp.data.repository

import android.location.Geocoder
import com.rahman.pemiluapp.data.model.CoordinateModel
import com.rahman.pemiluapp.data.model.VoterDataModel
import com.rahman.pemiluapp.data.sql.DatabaseHelper
import com.rahman.pemiluapp.domain.repositories.EntryDataRepository
import com.rahman.pemiluapp.utils.Response

class EntryDataRepositoryImpl(
    private val dbHelper: DatabaseHelper,
    private val geoCoder: Geocoder
) : EntryDataRepository {
    override fun getCurrentLocation(coordinate: CoordinateModel, onLocationResult: (Response) -> Unit) {
        try {
            val address = geoCoder.getFromLocation(coordinate.lat, coordinate.lng, 10)

            if (!address.isNullOrEmpty()) onLocationResult(Response.Success(address[0].getAddressLine(0)))
            else onLocationResult(Response.Failure())
        } catch (e: Exception) {
            onLocationResult(Response.Error(e.localizedMessage))
        }
    }

    override fun validateDataExists(nik: String, onResponse: (Response) -> Unit) {
        try {
            val voterData = dbHelper.getVoterByID(nik)

            if (voterData != null) onResponse(Response.Success(voterData.nik))
            else onResponse(Response.Failure())
        } catch (e: Exception) {
            onResponse(Response.Error(e.localizedMessage))
        }
    }

    override fun addVoterData(voterData: VoterDataModel, onResponse: (Response) -> Unit) {
        try {
            val addData = dbHelper.insertVoter(voterData)

            if (addData != -1L) onResponse(Response.Success())
            else onResponse(Response.Failure())
        } catch (e: Exception) {
            onResponse(Response.Error(e.localizedMessage))
        }
    }
}