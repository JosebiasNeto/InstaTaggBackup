package com.jnsoft.instatagg.presentation.adapter

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.jnsoft.instatagg.domain.model.Photo
import com.jnsoft.instatagg.presentation.fragments.FullscreenPhotoFragment

class FullscreenPhotoAdapter(activity: AppCompatActivity, val photos: ArrayList<Photo>):
      FragmentStateAdapter(activity){
    private var position = 0

    override fun getItemCount(): Int = photos.size

    override fun createFragment(position: Int): Fragment {
        this.position = position
        return FullscreenPhotoFragment.newInstance(photos[position])
    }

    fun addPhotos(fullscreenPhotos: List<Photo>){
        this.photos.apply {
            clear()
            addAll(fullscreenPhotos)
        }
    }
}