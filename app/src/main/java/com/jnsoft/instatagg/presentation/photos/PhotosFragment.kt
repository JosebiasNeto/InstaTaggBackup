package com.jnsoft.instatagg.presentation.photos

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.activity.addCallback
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
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
        viewModel.importPhotos(result)
        viewModel.getPhotos(tagg.id!!)
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
                    viewModel.delPhoto(it) }
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
                    it1.id!!)
                context!!.applicationContext.deleteFile(adapter.getPhoto(i).path!!
                    .substring(adapter.getPhoto(i).path!!.lastIndexOf("/")+1))
            }
        }
        view!!.findNavController().navigate(R.id.action_photosFragment_to_taggsFragment)
        eventDeleteTagg(adapter.itemCount)
    }

    private fun sharePhotos() {
        viewModel.sharePhotos()
    }

    private fun deletePhotos(){
        selectedPhotos.map {
            viewModel.delPhoto(it.id!!)
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