package com.rahman.pemiluapp.domain.repositories

import com.rahman.pemiluapp.domain.util.Response
import java.io.File

interface FileExportRepository {
    suspend fun exportToJson(file: File, onResponse: (Response<Boolean>) -> Unit)
    suspend fun importFromJson(file: File, onResponse: (Response<Boolean>) -> Unit)
}