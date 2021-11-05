package com.example.instatagg.presentation.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.instatagg.databinding.ActivityFullscreanPhotoBinding
import com.example.instatagg.domain.model.Photo
import com.squareup.picasso.Picasso

class FullscreanPhotoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFullscreanPhotoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFullscreanPhotoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val photo = intent.getParcelableExtra<Photo>("photo")!!
        Picasso.get().load(photo.path).noFade().into(binding.ivFullscreanPhoto)
    }
}