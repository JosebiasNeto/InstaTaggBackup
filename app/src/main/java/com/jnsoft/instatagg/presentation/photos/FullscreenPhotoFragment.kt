package com.jnsoft.instatagg.presentation.photos

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.jnsoft.instatagg.databinding.FragmentFullscreenPhotoBinding
import com.jnsoft.instatagg.domain.model.Photo
import com.jsibbold.zoomage.ZoomageView
import org.koin.androidx.viewmodel.ext.android.viewModel

class FullscreenPhotoFragment : Fragment() {
    private lateinit var binding: FragmentFullscreenPhotoBinding
    private lateinit var photo: Photo
    private lateinit var imageView: ZoomageView
    private lateinit var viewPager: ViewPager2
    private lateinit var pageAdapter: FullscreenPhotoAdapter
    private val viewModel: PhotosViewModel by viewModel()

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFullscreenPhotoBinding.inflate(layoutInflater)

        setFullscreenPhoto()
        setViewPager()

        return binding.root
    }

    private fun setFullscreenPhoto() {
        viewModel.photoFullscreen.observe(this,{
            photo = it
        })
        imageView = binding.ivFullscreanPhoto
        imageView.setImageURI(photo.path!!.toUri())
    }

    private fun setViewPager() {
        viewPager = binding.vpFullscreenPhoto
        pageAdapter = FullscreenPhotoAdapter(activity!! as AppCompatActivity, arrayListOf())
        viewPager.adapter = pageAdapter
        viewModel.photos.observe(this,{
            refreshPagerAdapter(it)
        })
    }

    private fun refreshPagerAdapter(photos: List<Photo>){
        pageAdapter.apply {
            addPhotos(photos)
            notifyDataSetChanged()
        }
    }
}