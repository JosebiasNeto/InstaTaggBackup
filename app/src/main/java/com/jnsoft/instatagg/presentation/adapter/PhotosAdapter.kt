package com.jnsoft.instatagg.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.jnsoft.instatagg.R
import com.jnsoft.instatagg.domain.model.Photo
import com.jnsoft.instatagg.presentation.activities.PhotosActivity
import com.squareup.picasso.Picasso

class PhotosAdapter(private val photos: ArrayList<Photo>, val activity: PhotosActivity, val width: Int) :
    RecyclerView.Adapter<PhotosAdapter.PhotosHolder>() {
    class PhotosHolder(itemView: View, private val activity: PhotosActivity, private val width: Int) : RecyclerView.ViewHolder(itemView) {
        private val ivPhoto = itemView.findViewById<ImageView>(R.id.iv_photo)
        private val checkBox = itemView.findViewById<CheckBox>(R.id.checkBox)
        fun bind(photo: Photo) {
            Picasso.get().load(photo.path).noFade().resize(width/3, width/3).into(ivPhoto)
            checkBox.isVisible = photo.checkboxVisibility
            checkBox.isChecked = photo.checked
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotosAdapter.PhotosHolder =
        PhotosAdapter.PhotosHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.photo_item, parent, false), activity, width)

    override fun onBindViewHolder(holder: PhotosHolder, position: Int) {
        holder.bind(photos[position])
        holder.itemView.setOnLongClickListener {
            PhotosAdapter(photos, activity, width).changeCheckBoxVisibility()
            photos[position].checked = true
            notifyDataSetChanged()
            true }
    }

    override fun getItemCount(): Int = photos.size

    fun getPhoto(position: Int) = photos[position]

    fun addPhotos(photos: List<Photo>) {
        this.photos.apply {
            clear()
            addAll(photos)
        }
    }
    fun changeCheckBoxVisibility(){
        if(getPhoto(0).checkboxVisibility) {
            photos.map { it.checkboxVisibility = false}
        } else photos.map { it.checkboxVisibility = true
        it.checked = false}
        activity.changeBottomOptionsVisibility()
    }
}