package com.example.behealthy.model.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import androidx.core.net.toUri
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

object Compressor {

    fun reduceImageSize(context: Context, uri: Uri, quality: Int = 50, format: Bitmap.CompressFormat = Bitmap.CompressFormat.JPEG, angle: Float = 0f): Uri {
        val inputStream = context.contentResolver.openInputStream(uri)
        var bitmap = BitmapFactory.decodeStream(inputStream)

        // Rotate the bitmap if needed
        if (bitmap.width > bitmap.height && angle != 0f) {
            val matrix = Matrix()
            matrix.postRotate(angle)
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        }
        // Compress the rotated bitmap and reduce its size
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(format, quality, byteArrayOutputStream)
        val compressedBitmap = BitmapFactory.decodeStream(ByteArrayInputStream(byteArrayOutputStream.toByteArray()))

        // Save the compressed image to a temporary file and return its URI
        val file = File.createTempFile("compressed", ".$format", context.cacheDir)
        val fileOutputStream = FileOutputStream(file)
        compressedBitmap.compress(format, quality, fileOutputStream)
        fileOutputStream.close()
        return file.toUri()
    }

}