package com.rahman.pemiluapp.utils

import android.content.Context
import android.net.Uri
import java.io.BufferedOutputStream
import java.io.File

object UriConverter {
    fun writeUriContentToFile(context: Context, uri: Uri, destinationFile: File, withBufferedOutput: Boolean = false): Boolean {
        return try {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                if (withBufferedOutput) {
                    BufferedOutputStream(destinationFile.outputStream())
                        .use { outputStream -> inputStream.copyTo(outputStream) }
                } else {
                    destinationFile.outputStream()
                        .use { outputStream -> inputStream.copyTo(outputStream) }
                }
            } ?: return false

            true
        } catch (_: Exception) {
            false
        }
    }

    fun writeUriToFile(uri: Uri?, context: Context, destinationFile: File, withBufferedOutput: Boolean = false): File? {
        uri ?: return null

        return try {
            if (writeUriContentToFile(context, uri, destinationFile, withBufferedOutput)) {
                destinationFile
            } else {
                destinationFile.delete()
                null
            }
        } catch (_: Exception) {
            destinationFile.delete()
            return null
        }
    }
}