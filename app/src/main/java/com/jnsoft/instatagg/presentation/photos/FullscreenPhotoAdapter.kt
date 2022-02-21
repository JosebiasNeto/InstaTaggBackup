package com.jnsoft.instatagg.presentation.photos

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.jnsoft.instatagg.domain.model.Photo

class FullscreenPhotoAdapter(activity: AppCompatActivity, val photos: ArrayList<Photo>
): FragmentStateAdapter(activity){

    private var position = 0

    override fun getItemCount(): Int = photos.size

    override fun createFragment(position: Int): Fragment {
        this.position = position
        return FullscreenImageFragment.newInstance(photos[position])
    }

    fun addPhotos(fullscreenPhotos: List<Photo>){
        this.photos.apply {
            clear()
            addAll(fullscreenPhotos)
        }
    }
}