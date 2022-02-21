package com.jnsoft.instatagg.presentation.photos

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import com.jnsoft.instatagg.databinding.FragmentFullscreenImageBinding
import com.jnsoft.instatagg.domain.model.Photo
import com.jsibbold.zoomage.ZoomageView

class FullscreenImageFragment : Fragment() {

    private lateinit var binding: FragmentFullscreenImageBinding
    private lateinit var imageView: ZoomageView
    private lateinit var photo: Photo

    companion object{
        fun newInstance(photo: Photo): FullscreenImageFragment {
            val fragment = FullscreenImageFragment()
            val savePhoto = Bundle()
            savePhoto.putParcelable("photo", photo)
            fragment.arguments = savePhoto
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentFullscreenImageBinding.inflate(inflater)
        photo = requireArguments().getParcelable("photo")!!
        setFullscreenPhoto(photo)

        return binding.root
    }

    private fun setFullscreenPhoto(photo: Photo) {
        imageView = binding.ivFullscreanImage
        imageView.setImageURI(photo.path!!.toUri())
    }
}