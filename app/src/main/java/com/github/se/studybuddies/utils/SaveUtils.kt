package com.github.se.studybuddies.utils

import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import com.github.se.studybuddies.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.URL

sealed class SaveType(val mimeType: String, val relativePath: String) {
    class Photo : SaveType("image/jpeg", "Pictures/YourAppName")
    class Text : SaveType("application/octet-stream", "Documents/YourAppName")

    class PDF : SaveType("application/pdf", "Documents/YourAppName")
    // Add more types as needed, e.g., class Video : SaveType("video/mp4", "Movies/YourAppName")
}

suspend fun saveToStorage(context: Context, uri: Uri, displayName: String, saveType: SaveType) {
    val resolver = context.contentResolver
    val appName = context.getString(R.string.app_name)
    val relativePath = saveType.relativePath.replace("YourAppName", appName)

    val uniqueDisplayName = "${displayName}_${System.currentTimeMillis()}"

    val contentValues = ContentValues().apply {
        put(MediaStore.MediaColumns.DISPLAY_NAME, uniqueDisplayName)
        put(MediaStore.MediaColumns.MIME_TYPE, saveType.mimeType)
        put(MediaStore.MediaColumns.RELATIVE_PATH, relativePath)
    }

    val externalUri = when (saveType) {
        is SaveType.Photo -> MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        is SaveType.Text, is SaveType.PDF -> MediaStore.Files.getContentUri("external")
    }

    val localFile = downloadFileFromUrl(context, uri.toString())

    localFile?.let {
        val destinationUri: Uri? = resolver.insert(externalUri, contentValues)
        if (destinationUri != null) {
            try {
                val inputStream = FileInputStream(localFile)
                if (saveType is SaveType.Photo) {
                    saveImage(inputStream, resolver, destinationUri)
                } else {
                    saveFile(inputStream, resolver, destinationUri)
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    /*val destinationUri: Uri? = resolver.insert(externalUri, contentValues)
    destinationUri?.let {
        copyUriContent(resolver, uri, it)
    }*/




    /*val destinationUri: Uri? = resolver.insert(externalUri, contentValues)
    if (destinationUri != null) {
        try {
            resolver.openInputStream(uri)?.use { inputStream ->
                if (saveType is SaveType.Photo) {
                    saveImage(inputStream, resolver, destinationUri)
                } else {
                    saveFile(inputStream, resolver, destinationUri)
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }*/
}

private fun saveImage(inputStream: InputStream, resolver: ContentResolver, destinationUri: Uri) {
    val bitmap = BitmapFactory.decodeStream(inputStream)
    resolver.openOutputStream(destinationUri)?.use { outputStream ->
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        outputStream.flush()
    }
}

private fun saveFile(inputStream: InputStream, resolver: ContentResolver, destinationUri: Uri) {
    resolver.openOutputStream(destinationUri)?.use { outputStream ->
        inputStream.copyTo(outputStream)
        outputStream.flush()
    }
}

fun copyUriContent(resolver: ContentResolver, sourceUri: Uri, destinationUri: Uri) {
    try {
        val outputStream: OutputStream? = resolver.openOutputStream(destinationUri)
        val inputStream: InputStream? = resolver.openInputStream(sourceUri)
        if (outputStream != null && inputStream != null) {
            inputStream.copyTo(outputStream)
            inputStream.close()
            outputStream.close()
        }
    } catch (e: IOException) {
        e.printStackTrace()
    }
}


private suspend fun downloadFileFromUrl(context: Context, fileUrl: String): File? {
    return withContext(Dispatchers.IO) {
        try {
            val url = URL(fileUrl)
            val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
            connection.connect()
            if (connection.responseCode != HttpURLConnection.HTTP_OK) {
                null
            } else {
                val file = File(context.cacheDir, "downloaded_temp_file")
                val inputStream: InputStream = connection.inputStream
                val outputStream: OutputStream = FileOutputStream(file)
                inputStream.copyTo(outputStream)
                inputStream.close()
                outputStream.close()
                file
            }
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }
}