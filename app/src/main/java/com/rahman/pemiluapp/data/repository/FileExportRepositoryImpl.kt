package com.rahman.pemiluapp.data.repository

import android.content.Context
import androidx.core.net.toUri
import com.rahman.pemiluapp.data.sql.DatabaseHelper
import com.rahman.pemiluapp.data.util.JsonUtil
import com.rahman.pemiluapp.domain.repositories.FileExportRepository
import com.rahman.pemiluapp.domain.util.Response
import java.io.File
import java.io.FileOutputStream

class FileExportRepositoryImpl(
    private val sqlDatabase: DatabaseHelper,
    private val context: Context
) : FileExportRepository {
    override suspend fun exportToJson(file: File, onResponse: (Response<Boolean>) -> Unit) {
        try {
            onResponse(Response.Loading)
            val data = sqlDatabase.getAllVoter()

            val imageDir = File(file.parentFile, JsonUtil.IMAGE_DIR).apply { mkdirs() }

            val votersWithCopiedImages = data.map { voter ->
                val uri = voter.gambar
                if (uri != null) {
                    val filename = "image_${voter.nik}.jpg"
                    val targetFile = File(imageDir, filename)

                    try {
                        val sourceUri = uri.toUri()
                        context.contentResolver.openInputStream(sourceUri)?.use { input ->
                            FileOutputStream(targetFile).use { output ->
                                input.copyTo(output)
                            }
                        }
                        voter//todo: maybe {.copy(gambar = "${JsonUtil.IMAGE_DIR}/$filename")}
                    } catch (_: Exception) {
                        voter//.copy(gambar = null)
                    }
                } else {
                    voter
                }
            }

            if (votersWithCopiedImages.isNotEmpty()) {
                JsonUtil.toJsonFile(votersWithCopiedImages, file)
                onResponse(Response.Success(true))
            } else onResponse(Response.Failure())
        } catch (e: Exception) {
            onResponse(Response.Error(e.localizedMessage))
        }
    }

    override suspend fun importFromJson(file: File, onResponse: (Response<Boolean>) -> Unit) {
        try {
            onResponse(Response.Loading)
            val importedVoters = JsonUtil.fromJsonFile(file)

            if (importedVoters.isNotEmpty()) {
                val existingVoters = sqlDatabase.getAllVoter()
                val existingMap = existingVoters.associateBy { it.nik }

                val votersToUpsert = importedVoters.filter { new ->
                    val existing = existingMap[new.nik]
                    existing == null || existing != new
                }

                if (votersToUpsert.isNotEmpty()) {
                    sqlDatabase.importVoters(votersToUpsert)
                    onResponse(Response.Success(true))
                } else onResponse(Response.Success(false))
            } else {
                onResponse(Response.Failure())
            }
        } catch (e: Exception) {
            onResponse(Response.Error(e.localizedMessage))
        }
    }
}