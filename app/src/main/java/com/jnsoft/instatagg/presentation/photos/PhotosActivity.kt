package com.jnsoft.instatagg.presentation.photos

import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.DisplayMetrics
import android.view.*
import android.widget.CheckBox
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.net.toFile
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.jnsoft.instatagg.R
import com.jnsoft.instatagg.databinding.ActivityPhotosBinding
import com.jnsoft.instatagg.domain.model.Photo
import com.jnsoft.instatagg.domain.model.Tagg
import com.jnsoft.instatagg.presentation.camera.CameraActivity
import com.jnsoft.instatagg.presentation.camera.MiniTaggsAdapter
import com.jnsoft.instatagg.presentation.taggs.EditTaggFragment
import com.jnsoft.instatagg.presentation.taggs.TaggsActivity
import com.jnsoft.instatagg.utils.FirebaseAnalytics.eventDeletePhoto
import com.jnsoft.instatagg.utils.FirebaseAnalytics.eventDeleteTagg
import com.jnsoft.instatagg.utils.FirebaseAnalytics.eventImportPhoto
import com.jnsoft.instatagg.utils.FirebaseAnalytics.eventSharePhoto
import com.jnsoft.instatagg.utils.OnItemClickListener
import com.jnsoft.instatagg.utils.addOnItemClickListener
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File

class PhotosActivity : AppCompatActivity(), EditTaggFragment.EditedTagg {
    private lateinit var binding: ActivityPhotosBinding
    private val viewModel: PhotosViewModel by viewModel()
    private lateinit var adapter: PhotosAdapter
    private lateinit var adapterMiniTaggs: MiniTaggsAdapter
    private lateinit var pageAdapter: FullscreenPhotoAdapter
    private lateinit var tagg: Tagg
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var viewPager: ViewPager2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPhotosBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        viewPager = binding.vpFullscreenPhoto
        pageAdapter = FullscreenPhotoAdapter(this, arrayListOf())
        viewPager.adapter = pageAdapter
        tagg = intent.getParcelableExtra<Tagg>("tagg")!!
        setTagg(tagg.id)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        setPhotosAdapter()
        binding.rvPhotos.adapter = adapter
        binding.rvPhotos.layoutManager = GridLayoutManager(this, 3)
        if (tagg != null) {
            tagg.id?.let {
                viewModel.getPhotos(it).observe(this, {
                    refreshAdapter(it.reversed())
                    refreshPagerAdapter(it.reversed())
                })
            }
        }
        binding.rvPhotos.addOnItemClickListener(object : OnItemClickListener {
            override fun onItemClicked(position: Int, view: View) {
                selectPhoto(position, view)
            }
        })
        binding.ibDelete.setOnClickListener {
            deletePhotos()
        }

        binding.ibShare.setOnClickListener {
            sharePhotos()
        }
        registerForContextMenu(binding.ibMore)
        binding.ibMore.setOnClickListener { openContextMenu(it) }

        binding.fabFromTaggToCamera.setOnClickListener {
            val mainActivity = Intent(this, CameraActivity::class.java)
            mainActivity.putExtra("tagg", tagg)
            startActivity(mainActivity)
            overridePendingTransition(0,0)
        }
        adapterMiniTaggs = MiniTaggsAdapter(arrayListOf())
        binding.rvChooseTagg.adapter = adapterMiniTaggs
        binding.rvChooseTagg.layoutManager = LinearLayoutManager(this)
        viewModel.getTaggs().observe(this, {
            refreshAdapterMain(it)
        })

        activityResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult())
        { result ->
            getImportPhotos(result)
        }
    }

    private fun selectPhoto(position: Int, view: View) {
        if(view.findViewById<CheckBox>(R.id.checkBox).isVisible){
            view.findViewById<CheckBox>(R.id.checkBox).isChecked =
                !view.findViewById<CheckBox>(R.id.checkBox).isChecked
            adapter.getPhoto(position).checked =
                !adapter.getPhoto(position).checked
        } else {
            openFullscreen(position)
        }
    }

    private fun openFullscreen(position: Int) {
        viewPager.setCurrentItem(position, false)
        binding.rvPhotos.visibility = View.GONE
        binding.vpFullscreenPhoto.visibility = View.VISIBLE
        changeBottomOptionsVisibility()

    }
    private fun checkCurrentPhotoViewPager(){
        if(binding.vpFullscreenPhoto.isVisible) adapter.getPhoto(viewPager.currentItem).checked = true
    }

    fun changeBottomOptionsVisibility(){
        binding.cvBottom.isVisible = !binding.cvBottom.isVisible
        binding.fabFromTaggToCamera.isVisible = !binding.fabFromTaggToCamera.isVisible
        binding.cvTotalSize.isVisible = !binding.cvTotalSize.isVisible
    }

    private fun refreshAdapter(photos: List<Photo>){
        adapter.apply {
            addPhotos(photos)
            notifyDataSetChanged()
        }
    }
    private fun refreshPagerAdapter(photos: List<Photo>){
        pageAdapter.apply {
            addPhotos(photos)
            notifyDataSetChanged()
        }
    }
    private fun refreshAdapterMain(taggs: List<Tagg>){
        adapterMiniTaggs.apply {
            addTaggs(taggs)
            notifyDataSetChanged()
        }
    }

    private fun setPhotosAdapter() {
        val outMetrics = DisplayMetrics()
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            val display = this.display
            display?.getRealMetrics(outMetrics)
        } else {
            @Suppress("DEPRECATION")
            val display = this.windowManager.defaultDisplay
            @Suppress("DEPRECATION")
            display.getMetrics(outMetrics)
        }
        adapter = PhotosAdapter(arrayListOf(), this, outMetrics.widthPixels)
    }

    private fun setTagg(id: Long?) {
        if (id != null) {
            viewModel.getTagg(id)
            viewModel.tagg.observe(this, {
                supportActionBar!!.title = it.name
                binding.toolbar.setBackgroundColor(it.color)
                if(it.size.toString().length > 6){
                    binding.tvTotalSize.text = it.size.toString().substring(0, it.size.toString().length - 6)
                } else binding.tvTotalSize.text = "0"
            })
        }
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
                onBackPressed()
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

    private fun getImportPhotos(result: ActivityResult) {
        if(result.resultCode == RESULT_OK){
            if (result.data!!.data != null){
                val uriPhoto = result.data!!.data!!
                val filePhoto = copyFile(File(getPath(applicationContext, uriPhoto)),
                    System.currentTimeMillis().toString() + ".jpg")
                viewModel.insertPhoto(Photo(filePhoto, tagg, null),
                    (filePhoto.toUri().toFile()).length())
                eventImportPhoto()
                finish()
                startActivity(this.intent)
            }
            else if (result.data!!.clipData != null){
                val newPhotos = arrayListOf<Uri>()
                val mClipData = result.data!!.clipData
                for (i in 0 until mClipData!!.itemCount){
                    newPhotos.add(mClipData.getItemAt(i).uri)
                }
                newPhotos.map {
                    val newPhoto = copyFile(File(getPath(applicationContext, it)),
                        System.currentTimeMillis().toString() + ".jpg")
                    viewModel.insertPhoto(Photo(newPhoto, tagg, null),
                        (newPhoto.toUri().toFile()).length())
                    eventImportPhoto()
                    finish()
                    startActivity(this.intent)
                }
            } else {
                Toast.makeText(this, "No images picked", Toast.LENGTH_LONG).show()
            }}
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

    private fun editTagg(tagg: Tagg){
        val editTaggFragment = EditTaggFragment.newInstance(tagg, this)
        editTaggFragment.show(supportFragmentManager,"editTagg")
    }
    override fun editTagg(name: String, color: Int) {
        tagg.id?.let { viewModel.changeTaggName(it, name) }
        tagg.id?.let { viewModel.changeTaggColor(it, color) }
        Handler().postDelayed({
            tagg.id?.let { viewModel.getTagg(it) }
        }, 500)
    }

    private fun clearTagg(tagg: Tagg){
        tagg.id?.let { viewModel.clearTagg(it) }
        for(i in 0 until adapter.itemCount){
            adapter.getPhoto(i).let { it1 ->
                viewModel.delPhoto(
                    it1,
                    (it1.path!!.toUri().toFile().length()))
                applicationContext.deleteFile(adapter.getPhoto(i).path!!
                    .substring(adapter.getPhoto(i).path!!.lastIndexOf("/")+1))
            }
        }
        finish()
        startActivity(intent)
    }

    private fun deleteTagg(tagg: Tagg){
        tagg.id?.let { viewModel.delTagg(it) }
        for(i in 0 until adapter.itemCount){
            adapter.getPhoto(i).let { it1 ->
                viewModel.delPhoto(
                    it1,
                    (it1.path!!.toUri().toFile().length()))
                applicationContext.deleteFile(adapter.getPhoto(i).path!!
                    .substring(adapter.getPhoto(i).path!!.lastIndexOf("/")+1))
            }
        }
        val taggsActivity = Intent(this, TaggsActivity::class.java)
        startActivity(taggsActivity)
        eventDeleteTagg(adapter.itemCount)
    }

    private fun sharePhotos() {
        checkCurrentPhotoViewPager()
        val contentUris = arrayListOf<Uri>()
        for(i in 0 until adapter.itemCount){
            var uri = FileProvider.getUriForFile(
                applicationContext,
                "com.jnsoft.instatagg.fileprovider",
                File(Uri.parse(adapter.getPhoto(i).path).path))
            if(adapter.getPhoto(i).checked){
                contentUris.add(uri)
                eventSharePhoto()
                adapter.getPhoto(i).checked = false
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

    private fun deletePhotos(){
        checkCurrentPhotoViewPager()
        for(i in 0 until adapter.itemCount){
            if(adapter.getPhoto(i).checked) {
                adapter.getPhoto(i).let { it1 ->
                    viewModel.delPhoto(it1,(it1.path!!.toUri()
                        .toFile().length()))
                }
                adapter.getPhoto(i).path?.let { it1 ->
                    applicationContext.deleteFile(adapter.getPhoto(i).path!!
                        .substring(adapter.getPhoto(i).path!!.lastIndexOf("/")+1))
                }
                eventDeletePhoto()
            }
        }
        finish()
        overridePendingTransition(0,0)
        startActivity(intent)
        overridePendingTransition(0,0)
    }

    override fun onCreateContextMenu(
            menu: ContextMenu?,
            v: View?,
            menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        super.onCreateContextMenu(menu, v, menuInfo)
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.photos_option_menu, menu)
        if(viewPager.isVisible){
            val selectAllMenu = menu!!.findItem(R.id.select_all)
            selectAllMenu.isVisible = false
        }
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.move_to_tagg -> {
                moveToTagg(this.tagg.id!!)
                return true
            }
            R.id.copy_to_tagg -> {
                copyToTagg()
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

    fun moveToTagg(oldTaggId: Long){
        checkCurrentPhotoViewPager()
        binding.cvChooseTagg.isVisible = true
        binding.rvChooseTagg.addOnItemClickListener(object: OnItemClickListener {
            override fun onItemClicked(position: Int, view: View) {
                for(i in 0 until adapter.itemCount){
                    if(adapter.getPhoto(i).checked){
                        val photo = adapter.getPhoto(i)
                        val tagg = adapterMiniTaggs.getTagg(position)
                        viewModel.movePhoto(tagg.name, tagg.color, tagg.id!!, photo.id!!,
                            (photo.path!!.toUri().toFile().length()), oldTaggId)
                    }
                }
                finish()
                startActivity(intent)
            }
        })
    }

    private fun copyToTagg() {
        checkCurrentPhotoViewPager()
        binding.cvChooseTagg.isVisible = true
        binding.rvChooseTagg.addOnItemClickListener(object: OnItemClickListener {
            override fun onItemClicked(position: Int, view: View) {
                for(i in 0 until adapter.itemCount){
                    if(adapter.getPhoto(i).checked) {
                        adapter.getPhoto(i).tagg = adapterMiniTaggs.getTagg(position)
                        adapter.getPhoto(i).path = copyFile(
                            adapter.getPhoto(i).path!!.toUri().toFile(),
                            System.currentTimeMillis().toString())
                        viewModel.insertPhoto(adapter.getPhoto(i),
                            (adapter.getPhoto(i).path!!.toUri().toFile().length()))
                    }
                }
                finish()
                startActivity(intent)
            }
        })
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
            finish()
        }
    }

    private fun copyFile(currentFile: File, newFileName: String): String {
        val newFile = File(applicationContext.filesDir, "$newFileName.jpg")
        return Uri.fromFile(currentFile.copyTo(newFile)).toString()
    }
}