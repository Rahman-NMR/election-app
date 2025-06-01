package com.rahman.pemiluapp.data.repository

import android.content.Context
import android.location.Geocoder
import android.net.Uri
import com.rahman.pemiluapp.data.model.CoordinateModel
import com.rahman.pemiluapp.data.model.VoterDataModel
import com.rahman.pemiluapp.data.sql.DatabaseHelper
import com.rahman.pemiluapp.domain.repositories.EntryDataRepository
import com.rahman.pemiluapp.domain.util.Response
import com.rahman.pemiluapp.utils.FileDirectoryOperation
import com.rahman.pemiluapp.utils.ImageOperation.reduceFileImage
import com.rahman.pemiluapp.utils.UriConverter

class EntryDataRepositoryImpl(
    private val dbHelper: DatabaseHelper,
    private val geoCoder: Geocoder,
    private val context: Context
) : EntryDataRepository {
    override suspend fun getCurrentLocation(coordinate: CoordinateModel, onLocationResult: (Response<String>) -> Unit) {
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

    override suspend fun validateDataExists(nik: String, onResponse: (Response<String>) -> Unit) {
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

    override suspend fun imageProcessing(uri: Uri?): Uri? {
        uri ?: return null

        val tempFile = FileDirectoryOperation.generateImageTempFile(context) ?: return null
        val imageFile = UriConverter.writeUriToFile(uri, context, tempFile, true) ?: return null
        val reducedImageFileInCache = imageFile.reduceFileImage()
        val finalImageFile = FileDirectoryOperation.moveFileToAppFileDir(context, reducedImageFileInCache)
        return finalImageFile?.let { Uri.fromFile(it) } ?: run {
            reducedImageFileInCache.delete()
            null
        }
    }

    override suspend fun addVoterData(voterData: VoterDataModel, onResponse: (Response<VoterDataModel>) -> Unit) {
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