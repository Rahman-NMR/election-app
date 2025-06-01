package com.rahman.pemiluapp.utils

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import com.rahman.pemiluapp.BuildConfig
import com.rahman.pemiluapp.data.util.JsonUtil
import com.rahman.pemiluapp.utils.DateFormatter.timeStampFilename
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

object FileDirectoryOperation {
    fun generateImageTempFile(context: Context): File? {
        return try {
            File.createTempFile(timeStampFilename, ".jpg", context.cacheDir)
        } catch (_: Exception) {
            null
        }
    }

    fun getUriFromFileTemp(context: Context, fileDirectory: File): Uri? {
        return try {
            FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID, fileDirectory)
        } catch (_: Exception) {
            null
        }
    }

    fun copyFileToAppDir(context: Context, sourceFile: File, desiredName: String? = null): File? {
        return try {
            val fileDir = context.getExternalFilesDir(JsonUtil.IMAGE_DIR)
            fileDir?.let { folder ->
                if (!folder.exists()) folder.mkdirs()
            }

            val destinationFileName = desiredName ?: "IMG_${sourceFile.name}"
            val destinationFile = File(fileDir, destinationFileName)

            FileInputStream(sourceFile).use { inputStream ->
                FileOutputStream(destinationFile).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }

            return destinationFile
        } catch (_: Exception) {
            return null
        }
    }

    fun moveFileToAppFileDir(context: Context, sourceFile: File, desiredName: String? = null): File? {
        val destinationFile = copyFileToAppDir(context, sourceFile, desiredName)
        if (destinationFile != null) {
            sourceFile.delete()
        }
        return destinationFile
    }
}