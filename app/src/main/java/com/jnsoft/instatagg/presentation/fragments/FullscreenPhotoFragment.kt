package com.jnsoft.instatagg.presentation.fragments

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ScaleGestureDetector
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toFile
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import com.davemorrissey.labs.subscaleview.ImageSource
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import com.jnsoft.instatagg.databinding.FragmentFullscreenPhotoBinding
import com.jnsoft.instatagg.domain.model.Photo

class FullscreenPhotoFragment() : Fragment() {
    private lateinit var binding: FragmentFullscreenPhotoBinding
    private lateinit var photo: Photo
    private lateinit var scaleGestureDetector: ScaleGestureDetector
    private var scaleFactor = 1.0f
    private lateinit var imageView: SubsamplingScaleImageView

    companion object {
        fun newInstance(photo: Photo): Fragment {
            val fragment = FullscreenPhotoFragment()
            val savePhoto = Bundle()
            savePhoto.putParcelable("photo", photo)
            fragment.arguments = savePhoto
            return fragment
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        photo = requireArguments().getParcelable<Photo>("photo")!!
        binding = FragmentFullscreenPhotoBinding.inflate(layoutInflater)
        val imageSize = getImageSize(photo)
        imageView = binding.ivFullscreanPhoto
        photo.path?.let { ImageSource.uri(it) }?.let { imageView.setImage(it) }
        return binding.root
    }

    private fun getImageSize(photo: Photo): ArrayList<Int>{
        val imageSize = arrayListOf<Int>()
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(photo.path!!.toUri().toFile().absolutePath, options)
        imageSize.add(options.outWidth)
        imageSize.add(options.outHeight)
        return imageSize
    }
}