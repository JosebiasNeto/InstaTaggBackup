package com.jnsoft.instatagg.presentation.photos

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.jnsoft.instatagg.databinding.FragmentFullscreenPhotoBinding
import com.jnsoft.instatagg.domain.model.Photo
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class FullscreenPhotoFragment() : Fragment() {
    private lateinit var binding: FragmentFullscreenPhotoBinding
    private lateinit var viewPager: ViewPager2
    private lateinit var pageAdapter: FullscreenPhotoAdapter
    private val viewModel: PhotosViewModel by sharedViewModel()

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFullscreenPhotoBinding.inflate(layoutInflater)

        setViewPager()

        return binding.root
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
}