package com.jnsoft.instatagg.presentation.photos

import android.app.Activity.RESULT_OK
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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.core.net.toFile
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.jnsoft.instatagg.R
import com.jnsoft.instatagg.databinding.FragmentPhotosBinding
import com.jnsoft.instatagg.domain.model.Photo
import com.jnsoft.instatagg.domain.model.Tagg
import com.jnsoft.instatagg.presentation.camera.MiniTaggsAdapter
import com.jnsoft.instatagg.presentation.taggs.EditTaggDialog
import com.jnsoft.instatagg.utils.FirebaseAnalytics.eventDeletePhoto
import com.jnsoft.instatagg.utils.FirebaseAnalytics.eventDeleteTagg
import com.jnsoft.instatagg.utils.FirebaseAnalytics.eventImportPhoto
import com.jnsoft.instatagg.utils.FirebaseAnalytics.eventSharePhoto
import com.jnsoft.instatagg.utils.OnItemClickListener
import com.jnsoft.instatagg.utils.addOnItemClickListener
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.io.File

class PhotosFragment : Fragment(), EditTaggDialog.EditedTagg {
    private lateinit var binding: FragmentPhotosBinding
    private val viewModel: PhotosViewModel by sharedViewModel()
    private lateinit var adapter: PhotosAdapter
    private lateinit var adapterMiniTaggs: MiniTaggsAdapter
    private val args: PhotosFragmentArgs by navArgs()
    private lateinit var tagg: Tagg
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var selectedPhotos: MutableList<Photo>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPhotosBinding.inflate(inflater, container, false)

        setToolbar()
        setTagg()
        setPhotos()
        setTaggs()
        setOnBackPressed()
        setBottomOptions()

        binding.fabFromTaggToCamera.setOnClickListener {
            it.findNavController().navigate(PhotosFragmentDirections
                .actionPhotosFragmentToCameraFragement(tagg.id!!))
        }

        activityResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult())
        { result ->
            getImportPhotos(result)
        }
        return binding.root
    }

    private fun setToolbar(){
        binding.tbPhotos.inflateMenu(R.menu.tagg_option_menu)
        binding.tbPhotos.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24)
        binding.tbPhotos.setNavigationOnClickListener {
           activity!!.onBackPressed()
        }
        binding.tbPhotos.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.import_photos -> {
                    val galery = Intent(Intent.ACTION_GET_CONTENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    galery.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                    galery.putExtra(Intent.EXTRA_LOCAL_ONLY, true)
                    galery.type = "image/*"
                    activityResultLauncher.launch(galery)
                    true
                }
                R.id.tagg_edit -> {
                    editTagg()
                    true
                }
                R.id.clear_tagg -> {
                    clearTagg()
                    true
                }
                R.id.delete_tagg -> {
                    deleteTagg()
                    true
                }
                else -> false
            }
        }
    }

    private fun setTagg() {
        viewModel.getTagg(args.taggid)
        viewModel.tagg.observe(this, {
            tagg = it
            binding.tbPhotos.title = it.name
            binding.tbPhotos.setBackgroundColor(it.color)
            if(it.size.toString().length > 6){
                binding.tvTotalSize.text = it.size.toString().substring(0, it.size.toString().length - 6)
            } else binding.tvTotalSize.text = "0"
        })

    }

    private fun setPhotos() {
        val outMetrics = DisplayMetrics()
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            val display = context!!.display
            display?.getRealMetrics(outMetrics)
        } else {
            @Suppress("DEPRECATION")
            val display = activity!!.windowManager.defaultDisplay
            @Suppress("DEPRECATION")
            display.getMetrics(outMetrics)
        }
        adapter = PhotosAdapter(arrayListOf(), this, outMetrics.widthPixels)
        binding.rvPhotos.adapter = adapter
        binding.rvPhotos.layoutManager = GridLayoutManager(context!!, 3)

        viewModel.getPhotos(args.taggid)
        viewModel.photos.observe(this, {
            refreshAdapter(it.reversed())
        })
        viewModel.photosSelected.observe(this, {
            selectedPhotos = it
        })
    }

    private fun setTaggs(){
        adapterMiniTaggs = MiniTaggsAdapter(arrayListOf())
        binding.rvChooseTagg.adapter = adapterMiniTaggs
        binding.rvChooseTagg.layoutManager = LinearLayoutManager(context!!)
        viewModel.getTaggs()
        viewModel.taggs.observe(this, {
            refreshAdapterMain(it)
        })
    }

    fun openFullscreenPhoto(position: Int){
        viewModel.setFullscreenPhoto(position)
        view!!.findNavController().navigate(R.id.action_photosFragment_to_fullscreenPhotoFragment)
    }

    fun selectPhoto(position: Int) {
        viewModel.addOrRemovePhotoSelected(adapter.getPhoto(position).id!!)
    }

    fun changeBottomOptionsVisibility(){
        binding.cvBottom.isVisible = !binding.cvBottom.isVisible
        binding.fabFromTaggToCamera.isVisible = !binding.fabFromTaggToCamera.isVisible
        binding.cvTotalSize.isVisible = !binding.cvTotalSize.isVisible
        if(adapter.getPhoto(0).checkboxVisibility){
            viewModel.setCheckboxPhotosInvisible()
            viewModel.uncheckAll()
            adapter.notifyDataSetChanged()
        } else {
            viewModel.setCheckboxPhotosVisible()
            adapter.notifyDataSetChanged()
        }
    }

    private fun refreshAdapter(photos: List<Photo>){
        adapter.apply {
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

    private fun getImportPhotos(result: ActivityResult) {
        if(result.resultCode == RESULT_OK){
            if (result.data!!.data != null){
                val uriPhoto = result.data!!.data!!
                val filePhoto = copyFile(File(getPath(context!!.applicationContext, uriPhoto)),
                    System.currentTimeMillis().toString() + ".jpg")
                viewModel.insertPhoto(Photo(filePhoto, tagg, null),
                    (filePhoto.toUri().toFile()).length())
                eventImportPhoto()
            }
            else if (result.data!!.clipData != null){
                val newPhotos = arrayListOf<Uri>()
                val mClipData = result.data!!.clipData
                for (i in 0 until mClipData!!.itemCount){
                    newPhotos.add(mClipData.getItemAt(i).uri)
                }
                newPhotos.map {
                    val newPhoto = copyFile(File(getPath(context!!.applicationContext, it)),
                        System.currentTimeMillis().toString() + ".jpg")
                    viewModel.insertPhoto(Photo(newPhoto, tagg, null),
                        (newPhoto.toUri().toFile()).length())
                    eventImportPhoto()

                }
            } else {
                Toast.makeText(context!!, "No images picked", Toast.LENGTH_LONG).show()
            }
        viewModel.getPhotos(tagg.id!!)
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

    private fun editTagg(){
        val editTaggFragment = EditTaggDialog.newInstance(tagg, this)
        editTaggFragment.show(activity!!.supportFragmentManager,"editTagg")
    }
    override fun editTagg(name: String, color: Int) {
        tagg.id?.let { viewModel.changeTaggName(it, name) }
        tagg.id?.let { viewModel.changeTaggColor(it, color) }
        Handler().postDelayed({
            tagg.id?.let { viewModel.getTagg(it) }
        }, 500)
    }

    private fun clearTagg(){
        tagg.id?.let { viewModel.clearTagg(it) }
        for(i in 0 until adapter.itemCount){
            adapter.getPhoto(i).let { it1 ->
                it1.id?.let {
                    viewModel.delPhoto(
                        it,
                        (it1.path!!.toUri().toFile().length()))
                }
                context!!.applicationContext.deleteFile(adapter.getPhoto(i).path!!
                    .substring(adapter.getPhoto(i).path!!.lastIndexOf("/")+1))
            }
        }
        viewModel.getPhotos(tagg.id!!)
    }

    private fun deleteTagg(){
        tagg.id?.let { viewModel.delTagg(it) }
        for(i in 0 until adapter.itemCount){
            adapter.getPhoto(i).let { it1 ->
                viewModel.delPhoto(
                    it1.id!!,
                    (it1.path!!.toUri().toFile().length()))
                context!!.applicationContext.deleteFile(adapter.getPhoto(i).path!!
                    .substring(adapter.getPhoto(i).path!!.lastIndexOf("/")+1))
            }
        }
        view!!.findNavController().navigate(R.id.action_photosFragment_to_taggsFragment)
        eventDeleteTagg(adapter.itemCount)
    }

    private fun sharePhotos() {
        val contentUris = arrayListOf<Uri>()
        selectedPhotos.map {
            var uri = FileProvider.getUriForFile(
            context!!.applicationContext,
            "com.jnsoft.instatagg.fileprovider",
            File(Uri.parse(it.path).path))
            contentUris.add(uri)
            eventSharePhoto()
        }
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND_MULTIPLE
            putParcelableArrayListExtra(Intent.EXTRA_STREAM, contentUris)
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            type = "image/jpg"
        }
        startActivity(Intent.createChooser(shareIntent, "shareImage"))
        viewModel.uncheckAll()
    }

    private fun deletePhotos(){
        selectedPhotos.map {
            viewModel.delPhoto(it.id!!,(it.path!!.toUri().toFile().length()))
            context!!.applicationContext.deleteFile(it.path!!
                .substring(it.path!!.lastIndexOf("/")+1))
            eventDeletePhoto()
        }
        viewModel.uncheckAll()
        Handler().postDelayed({
            viewModel.getPhotos(tagg.id!!)
        }, 500)
        changeBottomOptionsVisibility()
    }

    private fun setBottomOptions(){
        binding.ibDelete.setOnClickListener {
            deletePhotos()
        }
        binding.ibShare.setOnClickListener {
            sharePhotos()
        }
        binding.ibMore.setOnClickListener {
            val moreOptions = PopupMenu(activity!!, binding.rvPhotos)
            moreOptions.inflate(R.menu.photos_option_menu)
            moreOptions.show()
            moreOptions.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.move_to_tagg -> {
                        moveToTagg()
                        true
                    }
                    R.id.copy_to_tagg -> {
                        copyToTagg()
                        true
                    }
                    R.id.select_all -> {
                        viewModel.uncheckAll()
                        viewModel.checkAll()
                        adapter.notifyDataSetChanged()
                        true
                    }
                    else -> false
                }
            }
        }
    }

    fun moveToTagg(){
        binding.cvChooseTagg.isVisible = true
        binding.rvChooseTagg.addOnItemClickListener(object: OnItemClickListener {
            override fun onItemClicked(position: Int, view: View) {
                val newTagg = adapterMiniTaggs.getTagg(position)
                selectedPhotos.map {
                    viewModel.movePhoto(newTagg.name, newTagg.color, newTagg.id!!, it.id!!,
                        (it.path!!.toUri().toFile().length()), tagg.id!!)
                }
                binding.cvChooseTagg.isVisible = false
                changeBottomOptionsVisibility()
                viewModel.uncheckAll()
            }})
    }

    private fun copyToTagg() {
        binding.cvChooseTagg.isVisible = true
        binding.rvChooseTagg.addOnItemClickListener(object: OnItemClickListener {
            override fun onItemClicked(position: Int, view: View) {
                selectedPhotos.map {
                    it.tagg = adapterMiniTaggs.getTagg(position)
                    it.path = copyFile(it.path!!.toUri().toFile(),
                    System.currentTimeMillis().toString())
                    viewModel.insertPhoto(it, it.path!!.toUri().toFile().length())
                }
                binding.cvChooseTagg.isVisible = false
                changeBottomOptionsVisibility()
                viewModel.uncheckAll()
            }})
    }

    private fun copyFile(currentFile: File, newFileName: String): String {
        val newFile = File(context!!.applicationContext.filesDir, "$newFileName.jpg")
        return Uri.fromFile(currentFile.copyTo(newFile)).toString()
    }

    private fun setOnBackPressed(){
        activity!!.onBackPressedDispatcher.addCallback(this){
            if(binding.cvChooseTagg.isVisible){
                binding.cvChooseTagg.isVisible = false
            } else if(binding.cvBottom.isVisible){
                changeBottomOptionsVisibility()
                viewModel.setCheckboxPhotosInvisible()
                adapter.notifyDataSetChanged()
            } else {
                view!!.findNavController().navigate(R.id.action_photosFragment_to_taggsFragment)
            }
        }
    }
}