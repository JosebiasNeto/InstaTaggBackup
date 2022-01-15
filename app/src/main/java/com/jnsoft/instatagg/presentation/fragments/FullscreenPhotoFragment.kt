package com.jnsoft.instatagg.presentation.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import com.jnsoft.instatagg.databinding.FragmentFullscreenPhotoBinding
import com.jnsoft.instatagg.domain.model.Photo
import com.jsibbold.zoomage.ZoomageView

class FullscreenPhotoFragment : Fragment() {
    private lateinit var binding: FragmentFullscreenPhotoBinding
    private lateinit var photo: Photo
    private lateinit var imageView: ZoomageView

    companion object {
        fun newInstance(photo: Photo): FullscreenPhotoFragment{
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
        photo = requireArguments().getParcelable("photo")!!
        binding = FragmentFullscreenPhotoBinding.inflate(layoutInflater)
        imageView = binding.ivFullscreanPhoto
        imageView.setImageURI(photo.path!!.toUri())
        return binding.root
    }
}