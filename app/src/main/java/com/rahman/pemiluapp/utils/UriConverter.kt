package com.rahman.pemiluapp.utils

import android.content.Context
import android.net.Uri
import com.rahman.pemiluapp.utils.DateFormatter.currentTime
import com.rahman.pemiluapp.utils.DateFormatter.timeStamp
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

object UriConverter {
    fun uriToCachedFile(uri: Uri, context: Context): File? {
        val destinationFile = File(context.cacheDir, "imported_data_$currentTime.json")
        try {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                destinationFile.outputStream().use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }

            return destinationFile
        } catch (_: Exception) {
            if (destinationFile.exists()) {
                destinationFile.delete()
            }

            return null
        }
    }

    private fun createCustomTempFile(context: Context): File {
        val filesDir = context.externalCacheDir
        return File.createTempFile(timeStamp, ".jpg", filesDir)
    }

    fun uriToImageFile(imageUri: Uri, context: Context): File {
        val myFile = createCustomTempFile(context)
        val inputStream = context.contentResolver.openInputStream(imageUri) as InputStream
        val outputStream = FileOutputStream(myFile)
        val buffer = ByteArray(1024)
        var length: Int

        while (inputStream.read(buffer).also { length = it } > 0) outputStream.write(buffer, 0, length)
        outputStream.close()
        inputStream.close()

        return myFile
    }
}