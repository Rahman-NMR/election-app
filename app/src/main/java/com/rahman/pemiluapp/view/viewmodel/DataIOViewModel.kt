package com.rahman.pemiluapp.view.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rahman.pemiluapp.domain.usecase.DataUsecase
import com.rahman.pemiluapp.domain.util.Response
import kotlinx.coroutines.launch
import java.io.File

class DataIOViewModel(private val usecase: DataUsecase) : ViewModel() {
    fun exportToJson(file: File, onResponse: (Response<Boolean>) -> Unit) = viewModelScope.launch {
        usecase.exportData(file, onResponse)
    }

    fun importFromJson(file: File, onResponse: (Response<Boolean>) -> Unit) = viewModelScope.launch {
        usecase.importData(file, onResponse)
    }
}