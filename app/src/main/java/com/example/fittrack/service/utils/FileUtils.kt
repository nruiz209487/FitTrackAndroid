package com.example.fittrack.service.utils

import android.content.Context
import android.net.Uri
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

/**
 * La fucnion de FileUtils es permitir al usuario cojer una imagen de usario local
 */
object FileUtils {
    fun copyUriToInternalStorage(context: Context, uri: Uri): String {
        val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
        val directory = File(context.filesDir, "profile_images")
        if (!directory.exists()) {
            directory.mkdirs()
        }
        val file = File(directory, "user_profile_${System.currentTimeMillis()}.jpg")
        val outputStream = FileOutputStream(file)

        inputStream?.use { input ->
            outputStream.use { output ->
                input.copyTo(output)
            }
        }
        return file.absolutePath
    }
}