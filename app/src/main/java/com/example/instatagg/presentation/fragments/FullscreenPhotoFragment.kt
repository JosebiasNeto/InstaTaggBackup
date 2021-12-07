package com.example.instatagg.presentation.fragments

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ScaleGestureDetector
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.example.instatagg.databinding.FragmentFullscreenPhotoBinding
import com.example.instatagg.domain.model.Photo
import com.squareup.picasso.Picasso
import java.lang.Float.max
import java.lang.Float.min

class FullscreenPhotoFragment() : Fragment() {
    private lateinit var binding: FragmentFullscreenPhotoBinding
    private lateinit var photo: Photo
    private lateinit var scaleGestureDetector: ScaleGestureDetector
    private var scaleFactor = 1.0f
    private lateinit var imageView: ImageView

    companion object {
        fun newInstance(photo: Photo): Fragment {
            val fragment = FullscreenPhotoFragment()
            fragment.photo = photo
            return fragment
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFullscreenPhotoBinding.inflate(layoutInflater)
        Picasso.get().load(photo.path).noFade().resize(1440,1920)
            .into(binding.ivFullscreanPhoto)

        imageView = binding.ivFullscreanPhoto
        scaleGestureDetector = ScaleGestureDetector(context, ScaleListener())
        binding.root.setOnTouchListener { view, motionEvent ->
            scaleGestureDetector.onTouchEvent(motionEvent)
            return@setOnTouchListener true
        }

        return binding.root
    }



    private inner class ScaleListener : ScaleGestureDetector.SimpleOnScaleGestureListener(){
        @RequiresApi(Build.VERSION_CODES.N)
        override fun onScale(detector: ScaleGestureDetector?): Boolean {
            scaleFactor *= scaleGestureDetector.scaleFactor
            scaleFactor = max(0.1f, min(scaleFactor, 10.0f))
            imageView.scaleX = scaleFactor
            imageView.scaleY = scaleFactor
            return true
        }
    }

}