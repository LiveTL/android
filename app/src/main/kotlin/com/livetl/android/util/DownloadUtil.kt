package com.livetl.android.util

import android.Manifest
import android.annotation.TargetApi
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.app.ActivityCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import javax.inject.Inject

class DownloadUtil @Inject constructor(@ApplicationContext private val context: Context) {

    fun saveTextToStorage(text: String, fileName: String) {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        val fileStream = when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> getFileStreamImplApi29(fileName)
            else -> getFileStreamImpl(fileName)
        }

        fileStream.use {
            it.bufferedWriter().write(text)
        }
    }

    @TargetApi(Build.VERSION_CODES.Q)
    private fun getFileStreamImplApi29(fileName: String): OutputStream {
        val uri = context.contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
                put(MediaStore.Images.Media.MIME_TYPE, "text/plain")
                put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
            }
        )
        return context.contentResolver.openOutputStream(uri!!)!!
    }

    private fun getFileStreamImpl(fileName: String): OutputStream {
        val downloadsPath =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        return FileOutputStream(File(downloadsPath.toString(), fileName))
    }
}
