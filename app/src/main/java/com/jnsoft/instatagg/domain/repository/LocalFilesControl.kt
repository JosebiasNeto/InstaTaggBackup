package com.jnsoft.instatagg.domain.repository

import android.app.Activity
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.core.content.ContextCompat.startActivity
import androidx.core.content.FileProvider
import androidx.core.net.toFile
import androidx.core.net.toUri
import com.jnsoft.instatagg.di.InstaTagg.Companion.getAppContext
import com.jnsoft.instatagg.utils.FirebaseAnalytics
import com.jnsoft.instatagg.utils.FirebaseAnalytics.eventDeletePhoto
import org.koin.core.component.KoinComponent
import java.io.File

class LocalFilesControl(): KoinComponent {

    fun delFiles(path: String){
        getAppContext().deleteFile(path.substring(path
            .lastIndexOf("/")+1))
        eventDeletePhoto()
    }

    private fun copyFile(path: String): String{
        val newFile = File(getAppContext().filesDir,
            System.currentTimeMillis().toString())
        return Uri.fromFile(path.toUri().toFile().copyTo(newFile)).toString()
    }

    fun getFileSize(path: String): Long{
        return path.toUri().toFile().length()
    }

    fun getImportFiles(result: ActivityResult): ArrayList<String>{
        val files = arrayListOf<String>()
        if(result.resultCode == Activity.RESULT_OK){
            if (result.data!!.data != null){
                files.add(copyFile(getPath(getAppContext(), result.data!!.data!!)))
                FirebaseAnalytics.eventImportPhoto()
            }
            else if (result.data!!.clipData != null){
                val newPhotos = arrayListOf<Uri>()
                val mClipData = result.data!!.clipData
                for (i in 0 until mClipData!!.itemCount){
                    newPhotos.add(mClipData.getItemAt(i).uri)
                }
                newPhotos.map {
                    files.add(copyFile(getPath(getAppContext(), it)))
                    FirebaseAnalytics.eventImportPhoto()
                }
            } else {
                Toast.makeText(getAppContext(), "No images picked",
                    Toast.LENGTH_LONG).show()
            }
        }
        return files
    }
    private fun getPath(context: Context, uri: Uri): String {
        val isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT

        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            if (isExternalStorageDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":".toRegex()).toTypedArray()
                val type = split[0]
                if ("primary".equals(type, ignoreCase = true)) {
                    return Environment.getExternalStorageDirectory().toString() + "/" + split[1]
                }
            } else if (isDownloadsDocument(uri)) {
                val id = DocumentsContract.getDocumentId(uri)
                val contentUri = ContentUris.withAppendedId(
                    Uri.parse("content://downloads/public_downloads"), java.lang.Long.valueOf(id)
                )
                return getDataColumn(context, contentUri, null, null)
            } else if (isMediaDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":".toRegex()).toTypedArray()
                val type = split[0]
                var contentUri: Uri? = null
                if ("image" == type) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                } else if ("video" == type) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                } else if ("audio" == type) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                }
                val selection = "_id=?"
                val selectionArgs: Array<String?> = arrayOf(
                    split[1])
                return getDataColumn(context, contentUri, selection, selectionArgs)
            }
        } else if ("content".equals(uri.scheme, ignoreCase = true)) {
            return getDataColumn(context, uri, null, null)
        } else if ("file".equals(uri.scheme, ignoreCase = true)) {
            return uri.path!!
        }
        return ""
    }
    private fun getDataColumn(
        context: Context, uri: Uri?, selection: String?,
        selectionArgs: Array<String?>?
    ): String {
        var cursor: Cursor? = null
        val column = "_data"
        val projection = arrayOf(
            column
        )
        try {
            cursor = context.contentResolver.query(
                uri!!, projection, selection, selectionArgs,
                null
            )
            if (cursor != null && cursor.moveToFirst()) {
                val column_index: Int = cursor.getColumnIndexOrThrow(column)
                return cursor.getString(column_index)
            }
        } finally {
            if (cursor != null) cursor.close()
        }
        return ""
    }
    private fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalstorage.documents" == uri.authority
    }
    private fun isDownloadsDocument(uri: Uri): Boolean {
        return "com.android.providers.downloads.documents" == uri.authority
    }
    private fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents" == uri.authority
    }

    fun shareFiles(files: List<String>){
        val contentUris = arrayListOf<Uri>()
        files.map {
            var uri = FileProvider.getUriForFile(
                getAppContext(),
                "com.jnsoft.instatagg.fileprovider",
                File(Uri.parse(it).path))
            contentUris.add(uri)
            FirebaseAnalytics.eventSharePhoto()
        }
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND_MULTIPLE
            putParcelableArrayListExtra(Intent.EXTRA_STREAM, contentUris)
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK
            type = "image/jpg"
        }
        startActivity(getAppContext(), shareIntent, Bundle())
    }

}