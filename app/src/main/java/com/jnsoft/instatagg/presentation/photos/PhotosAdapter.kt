package com.jnsoft.instatagg.presentation.photos

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import androidx.core.net.toFile
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.jnsoft.instatagg.R
import com.jnsoft.instatagg.domain.model.Photo
import com.squareup.picasso.Picasso


class PhotosAdapter(private val photos: ArrayList<Photo>, val fragment: PhotosFragment, val width: Int) :
    RecyclerView.Adapter<PhotosAdapter.PhotosHolder>() {
    class PhotosHolder(itemView: View, private val width: Int) : RecyclerView.ViewHolder(itemView) {
        private val ivPhoto = itemView.findViewById<ImageView>(R.id.iv_photo)
        val checkBox = itemView.findViewById<CheckBox>(R.id.checkBox)
        fun bind(photo: Photo) {
            val portrait = portraitVerify(photo.path!!)
            if(portrait > 1){
                Picasso.get().load(photo.path).noFade().resize(0,
                    (width.toFloat() * portrait/3).toInt()).into(ivPhoto)
            } else {
                Picasso.get().load(photo.path).noFade().resize((width.toFloat()/(3*portrait))
                    .toInt(), 0).into(ivPhoto)
            }
            if(photo.checkboxVisibility){
                checkBox.isVisible = true
                checkBox.isChecked = !checkBox.isChecked
            } else {
                checkBox.isVisible = false
                checkBox.isChecked = true
            }
        }
        fun portraitVerify(photoPath: String):Float{
            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            BitmapFactory.decodeFile(photoPath.toUri().toFile().path, options)
            val imageHeight = options.outHeight.toFloat()
            val imageWidth = options.outWidth.toFloat()
            return imageHeight/imageWidth
        }
        fun setCheckbox(){
            checkBox.isChecked = !checkBox.isChecked
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotosHolder =
        PhotosHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.photo_item, parent, false), width)

    override fun onBindViewHolder(holder: PhotosHolder, position: Int) {
        holder.bind(photos[position])
        holder.itemView.setOnLongClickListener {
            fragment.changeBottomOptionsVisibility()
            if(!holder.checkBox.isVisible){
                holder.setCheckbox()
                fragment.selectPhoto(position)
            }
            true }

        holder.itemView.setOnClickListener {
            if(holder.checkBox.isVisible){
                holder.setCheckbox()
                fragment.selectPhoto(position)
            } else fragment.openFullscreenPhoto(position)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemCount(): Int = photos.size

    fun getPhoto(position: Int) = photos[position]

    fun addPhotos(photos: List<Photo>) {
        this.photos.apply {
            clear()
            addAll(photos)
        }
    }
}