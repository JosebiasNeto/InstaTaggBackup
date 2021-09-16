package com.example.instatagg.presentation.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.instatagg.databinding.ActivityPhotosBinding
import com.example.instatagg.presentation.viewmodel.MainViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class PhotosActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPhotosBinding
    private val viewModel: MainViewModel by viewModel()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPhotosBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}