package com.jnsoft.instatagg.presentation.photos

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.net.toFile
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.jnsoft.instatagg.R
import com.jnsoft.instatagg.databinding.FragmentFullscreenPhotoBinding
import com.jnsoft.instatagg.domain.model.Photo
import com.jnsoft.instatagg.domain.model.Tagg
import com.jnsoft.instatagg.presentation.camera.MiniTaggsAdapter
import com.jnsoft.instatagg.utils.FirebaseAnalytics
import com.jnsoft.instatagg.utils.OnItemClickListener
import com.jnsoft.instatagg.utils.addOnItemClickListener
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.io.File

class FullscreenPhotoFragment() : Fragment() {
    private lateinit var binding: FragmentFullscreenPhotoBinding
    private lateinit var viewPager: ViewPager2
    private lateinit var pageAdapter: FullscreenPhotoAdapter
    private val viewModel: PhotosViewModel by sharedViewModel()
    private lateinit var adapterMiniTaggs: MiniTaggsAdapter

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFullscreenPhotoBinding.inflate(layoutInflater)

        setToolbar()
        setViewPager()
        setTaggs()

        return binding.root
    }

    private fun setToolbar(){
        binding.tbFullscreenPhoto.inflateMenu(R.menu.fullscreen_option_menu)
        binding.tbFullscreenPhoto.setBackgroundColor(viewModel.tagg.value!!.color)
        binding.tbFullscreenPhoto.title = viewModel.tagg.value!!.name
        binding.tbFullscreenPhoto.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24)
        binding.tbFullscreenPhoto.setNavigationOnClickListener {
            activity!!.onBackPressed()
        }
        binding.tbFullscreenPhoto.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.delete_photo -> {
                    pageAdapter.getPhoto(viewPager.currentItem).let {
                        viewModel.delPhoto(it.id!!)
                        context!!.applicationContext.deleteFile(it.path!!
                            .substring(it.path!!.lastIndexOf("/")+1))
                        FirebaseAnalytics.eventDeletePhoto()
                        viewModel.getPhotos(it.tagg!!.id!!)
                        pageAdapter.notifyDataSetChanged()
                    }
                    true
                }
                R.id.share_photo -> {
                    val contentUris = arrayListOf<Uri>()
                    pageAdapter.getPhoto(viewPager.currentItem).let {
                        var uri = FileProvider.getUriForFile(
                            context!!.applicationContext,
                            "com.jnsoft.instatagg.fileprovider",
                            File(Uri.parse(it.path).path)
                        )
                        contentUris.add(uri)
                        FirebaseAnalytics.eventSharePhoto()
                    }
                    val shareIntent = Intent().apply {
                        action = Intent.ACTION_SEND_MULTIPLE
                        putParcelableArrayListExtra(Intent.EXTRA_STREAM, contentUris)
                        flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                        type = "image/jpg"
                    }
                    startActivity(Intent.createChooser(shareIntent, "shareImage"))
                    true
                }
                R.id.move_to_tagg -> {
                    binding.cvChooseTagg.isVisible = true
                    binding.rvChooseTagg.addOnItemClickListener(object: OnItemClickListener {
                        override fun onItemClicked(position: Int, view: View) {
                            pageAdapter.getPhoto(viewPager.currentItem).let {
                                val newTagg = adapterMiniTaggs.getTagg(position)
                                viewModel.movePhoto(
                                    newTagg.name, newTagg.color, newTagg.id!!, it.id!!,
                                    (it.path!!.toUri().toFile().length()), it.tagg!!.id!!)
                                binding.cvChooseTagg.isVisible = false
                            }}})
                    true
                }
                R.id.copy_to_tagg -> {
                    binding.cvChooseTagg.isVisible = true
                    binding.rvChooseTagg.addOnItemClickListener(object: OnItemClickListener {
                        override fun onItemClicked(position: Int, view: View) {
                            pageAdapter.getPhoto(viewPager.currentItem).let {
                                it.tagg = adapterMiniTaggs.getTagg(position)
                                it.path = copyFile(it.path!!.toUri().toFile(),
                                    System.currentTimeMillis().toString())
                                viewModel.insertPhoto(it, it.path!!.toUri().toFile().length())
                            }
                            binding.cvChooseTagg.isVisible = false
                        }})
                    true
                }
                else -> false
            }
        }
    }

    private fun setViewPager() {
        pageAdapter = FullscreenPhotoAdapter(activity!! as AppCompatActivity, arrayListOf())
        viewPager = binding.vpFullscreenPhoto
        viewPager.adapter = pageAdapter
        viewModel.photos.observe(this, {
            refreshPagerAdapter(it.reversed())
        })
    }
    private fun refreshPagerAdapter(photos: List<Photo>){
        pageAdapter.apply {
            addPhotos(photos)
            notifyDataSetChanged()
            showPhoto()
        }
    }
    fun showPhoto(){
        viewPager.setCurrentItem(viewModel.photoFullscreen.value!!, false)
        viewPager.isVisible = true
    }

    private fun copyFile(currentFile: File, newFileName: String): String {
        val newFile = File(context!!.applicationContext.filesDir, "$newFileName.jpg")
        return Uri.fromFile(currentFile.copyTo(newFile)).toString()
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
    private fun refreshAdapterMain(taggs: List<Tagg>){
        adapterMiniTaggs.apply {
            addTaggs(taggs)
            notifyDataSetChanged()
        }
    }
}