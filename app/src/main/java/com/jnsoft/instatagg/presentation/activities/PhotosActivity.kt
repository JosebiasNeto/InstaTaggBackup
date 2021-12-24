package com.jnsoft.instatagg.presentation.activities

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
import android.view.*
import android.widget.CheckBox
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.net.toFile
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.jnsoft.instatagg.R
import com.jnsoft.instatagg.databinding.ActivityPhotosBinding
import com.jnsoft.instatagg.domain.model.Photo
import com.jnsoft.instatagg.domain.model.Tagg
import com.jnsoft.instatagg.presentation.adapter.MainAdapter
import com.jnsoft.instatagg.presentation.adapter.PhotosAdapter
import com.jnsoft.instatagg.presentation.fragments.EditTaggFragment
import com.jnsoft.instatagg.presentation.viewmodel.PhotosViewModel
import com.jnsoft.instatagg.utils.OnItemClickListener
import com.jnsoft.instatagg.utils.addOnItemClickListener
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.*
import java.util.*

class PhotosActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPhotosBinding
    private val viewModel: PhotosViewModel by viewModel()
    private lateinit var adapter: PhotosAdapter
    private lateinit var adapterMain: MainAdapter
    private lateinit var tagg: Tagg
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPhotosBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        tagg = intent.getParcelableExtra<Tagg>("tagg")!!
        tagg.id?.let {
            viewModel.getTagg(it).observe(this,{
                supportActionBar!!.title = it.name
                binding.toolbar.setBackgroundColor(it.color)
                binding.tvTotalSize.text = it.size.toString()
            })
        }
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        adapter = PhotosAdapter(arrayListOf(), this)
        binding.rvPhotos.adapter = adapter
        binding.rvPhotos.layoutManager = GridLayoutManager(this, 3)
        if (tagg != null) {
            tagg.id?.let {
                viewModel.getPhotos(it).observe(this, {
                    refreshAdapter(it)
                })
            }
        }
        binding.rvPhotos.addOnItemClickListener(object : OnItemClickListener {
            override fun onItemClicked(position: Int, view: View) {
                if(view.findViewById<CheckBox>(R.id.checkBox).isVisible){
                    view.findViewById<CheckBox>(R.id.checkBox).isChecked =
                            !view.findViewById<CheckBox>(R.id.checkBox).isChecked
                   adapter.getPhoto(position).checked =
                           !adapter.getPhoto(position).checked
                } else {
                    openFullscreen(position)
                    this@PhotosActivity.finish()
                }
            }
        })
        binding.ibDelete.setOnClickListener {
            for(i in 0 until adapter.itemCount){
                if(adapter.getPhoto(i).checked) {
                    adapter.getPhoto(i).let { it1 ->
                        viewModel.delPhoto(it1,(it1.path!!.toUri()
                            .toFile().length()/(1024*1024)).toInt())
                    }
                    adapter.getPhoto(i).path?.let { it1 ->
                                applicationContext.deleteFile(adapter.getPhoto(i).path!!
                                    .substring(adapter.getPhoto(i).path!!.lastIndexOf("/")+1))
                }}
            }
            finish()
            overridePendingTransition(0,0)
            startActivity(intent)
            overridePendingTransition(0,0)
        }

        binding.ibShare.setOnClickListener {
            val contentUris = arrayListOf<Uri>()
            for(i in 0 until adapter.itemCount){
                var uri = FileProvider.getUriForFile(
                    applicationContext,
                    "com.jnsoft.instatagg.fileprovider",
                    File(Uri.parse(adapter.getPhoto(i).path).path))
                if(adapter.getPhoto(i).checked){
                    contentUris.add(uri)
                }
            }
            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND_MULTIPLE
                putParcelableArrayListExtra(Intent.EXTRA_STREAM, contentUris)
                flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                type = "image/jpg"
            }
            startActivity(Intent.createChooser(shareIntent, "shareImage"))
        }
        registerForContextMenu(binding.ibMore)
        binding.ibMore.setOnClickListener { openContextMenu(it) }

        binding.fabFromTaggToCamera.setOnClickListener {
            val mainActivity = Intent(this, MainActivity::class.java)
            mainActivity.putExtra("tagg", tagg)
            startActivity(mainActivity)
            overridePendingTransition(0,0)
        }
        adapterMain = MainAdapter(arrayListOf())
        binding.rvChooseTagg.adapter = adapterMain
        binding.rvChooseTagg.layoutManager = LinearLayoutManager(this)
        viewModel.getTaggs().observe(this, {
            refreshAdapterMain(it)
        })

        activityResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult())
        { result ->
            if(result.resultCode == RESULT_OK){
                if (result.data!!.data != null){
                    val uriPhoto = result.data!!.data!!
                    val filePhoto = copyFile(File(getPath(applicationContext, uriPhoto)), System.currentTimeMillis().toString() + ".jpg")
                    viewModel.insertPhoto(Photo(filePhoto, tagg, null),
                        (filePhoto.toUri().toFile().length()/(1024*1024)).toInt())
                }
            else if (result.data!!.clipData != null){
                val newPhotos = arrayListOf<Uri>()
                val mClipData = result.data!!.clipData
                for (i in 0 until mClipData!!.itemCount){
                    newPhotos.add(mClipData.getItemAt(i).uri)
                }
                newPhotos.map {
                    val newPhoto = copyFile(File(getPath(applicationContext, it)), System.currentTimeMillis().toString() + ".jpg")
                    viewModel.insertPhoto(Photo(newPhoto, tagg, null),
                        (newPhoto.toUri().toFile().length()/(1024*1024)).toInt())
                }
            } else {
                Toast.makeText(this, "No images picked", Toast.LENGTH_LONG).show()
            }}
        }
    }
    fun getPath(context: Context, uri: Uri): String {
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
    fun getDataColumn(
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

    fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalstorage.documents" == uri.authority
    }

    fun isDownloadsDocument(uri: Uri): Boolean {
        return "com.android.providers.downloads.documents" == uri.authority
    }

    fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents" == uri.authority
    }

    fun changeBottomOptionsVisibility(){
        binding.cvBottom.isVisible = !binding.cvBottom.isVisible
        binding.fabFromTaggToCamera.isVisible = !binding.fabFromTaggToCamera.isVisible
        binding.cvTotalSize.isVisible = !binding.cvTotalSize.isVisible
    }

    private fun openFullscreen(position: Int) {
        val fullscreenPhotoActivity = Intent(this, FullscreenPhotoActivity::class.java)
        val photo = adapter.getPhoto(position)
        fullscreenPhotoActivity.putExtra("photo", photo)
        fullscreenPhotoActivity.putExtra("position", position)
        startActivity(fullscreenPhotoActivity)
        overridePendingTransition(0,0)
    }

    private fun refreshAdapter(photos: List<Photo>){
        adapter.apply {
            addPhotos(photos)
            notifyDataSetChanged()
        }
    }

    private fun clearTagg(tagg: Tagg){
        tagg.id?.let { viewModel.clearTagg(it) }
        finish()
        startActivity(intent)
    }

    private fun deleteTagg(tagg: Tagg){
        tagg.id?.let { viewModel.delTagg(it) }
        for(i in 0 until adapter.itemCount){
                adapter.getPhoto(i).let { it1 ->
                        viewModel.delPhoto(
                            it1,
                            (it1.path!!.toUri().toFile().length()/(1024*1024)).toInt())
                                applicationContext.deleteFile(adapter.getPhoto(i).path!!
                                    .substring(adapter.getPhoto(i).path!!.lastIndexOf("/")+1))
                }
        }
        val taggsActivity = Intent(this, TaggsActivity::class.java)
        startActivity(taggsActivity)
    }
    fun editTagg(tagg: Tagg){
        val editTaggFragment = EditTaggFragment(tagg)
        editTaggFragment.show(supportFragmentManager,"editTagg")
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.tagg_option_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val tagg = intent.getParcelableExtra<Tagg>("tagg")!!
        when (item.itemId) {
            R.id.import_photos -> {
                val galery = Intent(Intent.ACTION_GET_CONTENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                galery.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                galery.putExtra(Intent.EXTRA_LOCAL_ONLY, true)
                galery.type = "image/*"
                activityResultLauncher.launch(galery)
                }

            android.R.id.home -> {
                startActivity(Intent(this, TaggsActivity::class.java))
                return true
            }
            R.id.tagg_edit -> {
                editTagg(tagg)
                return true
            }
            R.id.clear_tagg -> {
                clearTagg(tagg)
                return true
            }
            R.id.delete_tagg -> {
                deleteTagg(tagg)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateContextMenu(
            menu: ContextMenu?,
            v: View?,
            menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        super.onCreateContextMenu(menu, v, menuInfo)
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.photos_option_menu, menu)
    }
    private fun refreshAdapterMain(taggs: List<Tagg>){
        adapterMain.apply {
            addTaggs(taggs)
            notifyDataSetChanged()
        }
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.move_to_tagg -> {
                binding.cvChooseTagg.isVisible = true
                binding.rvChooseTagg.addOnItemClickListener(object: OnItemClickListener {
                    override fun onItemClicked(position: Int, view: View) {
                        for(i in 0 until adapter.itemCount){
                            if(adapter.getPhoto(i).checked)
                                moveToTagg(adapterMain.getTagg(position), adapter.getPhoto(i))
                        }
                        finish()
                        startActivity(intent)
                    }
                })
                return true
            }
            R.id.copy_to_tagg -> {
                binding.cvChooseTagg.isVisible = true
                binding.rvChooseTagg.addOnItemClickListener(object: OnItemClickListener {
                    override fun onItemClicked(position: Int, view: View) {
                        for(i in 0 until adapter.itemCount){
                            if(adapter.getPhoto(i).checked) {
                                adapter.getPhoto(i).tagg = adapterMain.getTagg(position)
                                adapter.getPhoto(i).path = copyFile(
                                adapter.getPhoto(i).path!!.toUri().toFile(),
                                System.currentTimeMillis().toString())
                                viewModel.insertPhoto(adapter.getPhoto(i),
                                    (adapter.getPhoto(i).path!!.toUri().toFile().length()/(1024*1024)).toInt())
                            }
                        }
                        finish()
                        startActivity(intent)
                    }
                })
                return true
            }
            R.id.select_all -> {
                for(i in 0 until adapter.itemCount){
                    adapter.getPhoto(i).checked = true
                    adapter.notifyDataSetChanged()
                }
                return true
            }
        }
        return super.onContextItemSelected(item)
    }

    private fun copyFile(currentFile: File, newFileName: String): String {
        val newFile = File(applicationContext.filesDir, "$newFileName.jpg")
        return Uri.fromFile(currentFile.copyTo(newFile)).toString()
    }

    fun moveToTagg(tagg: Tagg, photo: Photo){
        viewModel.movePhoto(tagg.name, tagg.color, tagg.id!!, photo.id!!,
        (photo.path!!.toUri().toFile().length()/(1024*1024)).toInt(), this.tagg.id!!)
    }

    override fun onBackPressed() {
        if(binding.cvChooseTagg.isVisible){
            binding.cvChooseTagg.visibility = View.GONE
        } else if(binding.cvBottom.isVisible){
            startActivity(intent)
            overridePendingTransition(0,0)
        } else {
            startActivity(Intent(this, TaggsActivity::class.java))
            overridePendingTransition(0,0)
        }
    }
}