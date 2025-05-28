package com.rahman.pemiluapp.view.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.rahman.pemiluapp.di.Injection
import com.rahman.pemiluapp.domain.usecase.DataUsecase

class ViewModelFactory private constructor(private val usecase: DataUsecase) : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(EntryDataViewModel::class.java) -> EntryDataViewModel(usecase) as T
            modelClass.isAssignableFrom(ShowDataViewModel::class.java) -> ShowDataViewModel(usecase) as T
            modelClass.isAssignableFrom(DataIOViewModel::class.java) -> DataIOViewModel(usecase) as T
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }

    companion object {
        fun getInstance(context: Context): ViewModelFactory = ViewModelFactory(Injection.provideUsecase(context))
    }
}