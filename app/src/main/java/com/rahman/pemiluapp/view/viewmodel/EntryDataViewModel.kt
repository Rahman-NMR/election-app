package com.rahman.pemiluapp.view.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.rahman.pemiluapp.data.model.CoordinateModel
import com.rahman.pemiluapp.data.model.VoterDataModel
import com.rahman.pemiluapp.domain.usecase.DataUsecase
import com.rahman.pemiluapp.domain.util.Response

class EntryDataViewModel(private val usecase: DataUsecase) : ViewModel() {
    private val _imgUri = MutableLiveData<Uri?>()
    val currentImageUri: LiveData<Uri?> get() = _imgUri

    private val _timestamp = MutableLiveData<Long>()
    val timestamp: LiveData<Long> get() = _timestamp

    fun saveImageUri(uri: Uri?) {
        _imgUri.value = uri
    }

    fun deleteImageUri() {
        _imgUri.value = null
    }

    fun saveTimestamp(timestamp: Long) {
        _timestamp.value = timestamp
    }

    fun getCurrentLocation(coordinate: CoordinateModel, onResponse: (Response<String>) -> Unit) {
        usecase.getCurrentLocation(coordinate, onResponse)
    }

    fun nullChecker(votersData: VoterDataModel, isGenderSelected: Int, onResponse: (Response<VoterDataModel>) -> Unit) {
        try {
            val voterData = votersData.copy(gambar = currentImageUri.value.toString(), tanggal = timestamp.value.toString())

            when {
                votersData.nik.isNullOrEmpty() -> onResponse(Response.Failure(NIK))
                votersData.nama.isNullOrEmpty() -> onResponse(Response.Failure(NAMA))
                votersData.nohp.isNullOrEmpty() -> onResponse(Response.Failure(NOHP))
                isGenderSelected == -1 -> onResponse(Response.Failure(GENDER))
                timestamp.value == null -> onResponse(Response.Failure(TANGGAL))
                votersData.alamat.isNullOrEmpty() -> onResponse(Response.Failure(ALAMAT))
                currentImageUri.value == null -> onResponse(Response.Failure(GAMBAR))
                else -> onResponse(Response.Success(voterData))
            }
        } catch (e: Exception) {
            onResponse(Response.Error(e.localizedMessage))
        }
    }

    fun isDataExist(nik: String, onResponse: (Response<String>) -> Unit) {
        usecase.validateDataExists(nik, onResponse)
    }

    fun addNewVoter(voterData: VoterDataModel, onResponse: (Response<VoterDataModel>) -> Unit) {
        usecase.addVoterData(voterData, onResponse)
    }

    companion object {
        const val NIK = "nik"
        const val NAMA = "nama"
        const val NOHP = "nohp"
        const val TANGGAL = "tanggal"
        const val ALAMAT = "alamat"
        const val GENDER = "gender"
        const val GAMBAR = "gambar"
    }
}