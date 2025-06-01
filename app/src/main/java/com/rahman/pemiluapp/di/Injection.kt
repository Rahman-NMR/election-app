package com.rahman.pemiluapp.di

import android.content.Context
import android.location.Geocoder
import com.rahman.pemiluapp.data.repository.EntryDataRepositoryImpl
import com.rahman.pemiluapp.data.repository.FileExportRepositoryImpl
import com.rahman.pemiluapp.data.repository.ShowDataRepositoryImpl
import com.rahman.pemiluapp.data.sql.DatabaseHelper
import com.rahman.pemiluapp.domain.interactor.DataInteractor
import com.rahman.pemiluapp.domain.repositories.EntryDataRepository
import com.rahman.pemiluapp.domain.repositories.FileExportRepository
import com.rahman.pemiluapp.domain.repositories.ShowDataRepository
import com.rahman.pemiluapp.domain.usecase.DataUsecase
import java.util.Locale

object Injection {
    private fun geoCoder(context: Context) = Geocoder(context, Locale.getDefault())
    private fun sqlDatabase(context: Context) = DatabaseHelper(context)

    fun entryDataRepository(context: Context): EntryDataRepository {
        return EntryDataRepositoryImpl(sqlDatabase(context), geoCoder(context), context)
    }

    fun showDataRepository(context: Context): ShowDataRepository {
        return ShowDataRepositoryImpl(sqlDatabase(context))
    }

    fun fileExportRepository(context: Context): FileExportRepository {
        return FileExportRepositoryImpl(sqlDatabase(context), context)
    }

    fun provideUsecase(context: Context): DataUsecase {
        val entryData = entryDataRepository(context)
        val showData = showDataRepository(context)
        val fileExport = fileExportRepository(context)
        return DataInteractor(entryData, showData, fileExport)
    }
}