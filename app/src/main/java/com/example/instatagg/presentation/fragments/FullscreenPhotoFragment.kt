package com.example.instatagg.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.instatagg.databinding.FragmentFullscreenPhotoBinding
import com.example.instatagg.domain.model.Photo
import com.squareup.picasso.Picasso

class FullscreenPhotoFragment() : Fragment() {
    private lateinit var binding: FragmentFullscreenPhotoBinding
    private lateinit var photo: Photo

    companion object {
        fun newInstance(photo: Photo): Fragment {
            val fragment = FullscreenPhotoFragment()
            fragment.photo = photo
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFullscreenPhotoBinding.inflate(layoutInflater)
        Picasso.get().load(photo.path).noFade().resize(1440,1920)
            .into(binding.ivFullscreanPhoto)
        return binding.root
    }

}