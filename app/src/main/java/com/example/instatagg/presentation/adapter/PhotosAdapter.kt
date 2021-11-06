package com.example.instatagg.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.instatagg.R
import com.example.instatagg.domain.model.Photo
import com.squareup.picasso.Picasso

class PhotosAdapter(private val photos: ArrayList<Photo>) :
    RecyclerView.Adapter<PhotosAdapter.PhotosHolder>() {

    class PhotosHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(photo: Photo) {
            Picasso.get().load(photo.path).noFade().resize(235,235)
                .into(itemView.findViewById<ImageView>(R.id.iv_photo))
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotosHolder =
        PhotosHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.photo_item, parent, false)
        )

    override fun onBindViewHolder(holder: PhotosHolder, position: Int) =
        holder.bind(photos[position])

    override fun getItemCount(): Int = photos.size

    fun getPhoto(position: Int) = photos[position]

    fun addPhotos(photos: List<Photo>) {
        this.photos.apply {
            clear()
            addAll(photos)
        }
    }

}