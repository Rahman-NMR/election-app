package com.rahman.pemiluapp.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import androidx.exifinterface.media.ExifInterface
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

object ImageOperation {
    private const val MAX_SIZE = 500000 //500 KB

    fun File.reduceFileImage(): File {
        val targetFile = this
        val bitmap = BitmapFactory.decodeFile(targetFile.path)?.getRotatedBitmap(targetFile)
            ?: return targetFile

        var compressQuality = 100
        var streamLength: Int

        try {
            do {
                ByteArrayOutputStream().use { bmpStream ->
                    bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpStream)

                    val bmpPicByteArray = bmpStream.toByteArray()
                    streamLength = bmpPicByteArray.size
                    compressQuality -= 5
                }
            } while (streamLength > MAX_SIZE && compressQuality > 0)

            val finalCompressQuality = if (compressQuality < 0) 0 else compressQuality
            FileOutputStream(targetFile).use { fos ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, finalCompressQuality, fos)
            }
        } catch (_: Exception) {
            return targetFile
        }
        return targetFile
    }

    private fun Bitmap.getRotatedBitmap(file: File): Bitmap {
        if (file.exists()) {
            val orientation = ExifInterface(file).getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED)
            return when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(this, 90F)
                ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(this, 180F)
                ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(this, 270F)
                ExifInterface.ORIENTATION_NORMAL -> this
                else -> this
            }
        }

        return this
    }

    private fun rotateImage(source: Bitmap, angle: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(angle)
        return Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
    }
}