package com.rahman.pemiluapp.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import androidx.core.content.FileProvider
import androidx.exifinterface.media.ExifInterface
import com.rahman.pemiluapp.BuildConfig
import com.rahman.pemiluapp.utils.DateFormatter.timeStamp
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

object ImageOperation {
    private const val MAXIMAL_SIZE = 1000000
    const val FILENAME_FORMAT = "yyyyMMdd_HHmmss-SSS"

    fun File.reduceFileImage(): File {
        val file = this
        val bitmap = BitmapFactory.decodeFile(file.path).getRotatedBitmap(file)
        var compressQuality = 100
        var streamLength: Int

        do {
            val bmpStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpStream)

            val bmpPicByteArray = bmpStream.toByteArray()
            streamLength = bmpPicByteArray.size
            compressQuality -= 5
        } while (streamLength > MAXIMAL_SIZE)

        bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, FileOutputStream(file))
        return file
    }

    fun getImageDir(context: Context): Uri {
        val tempImageDir = File(context.filesDir, "$timeStamp.jpg")
        return FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID, tempImageDir)
    }

    fun reImaged(context: Context, reducedImage: File): Uri? {
        return FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID, reducedImage)
    }

    private fun Bitmap.getRotatedBitmap(file: File): Bitmap {
        val orientation = ExifInterface(file).getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED)
        return when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(this, 90F)
            ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(this, 180F)
            ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(this, 270F)
            ExifInterface.ORIENTATION_NORMAL -> this
            else -> this
        }
    }

    private fun rotateImage(source: Bitmap, angle: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(angle)
        return Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
    }
}