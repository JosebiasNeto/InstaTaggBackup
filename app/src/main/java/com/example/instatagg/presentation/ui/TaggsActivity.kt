package com.example.instatagg.presentation.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.instatagg.databinding.ActivityTaggsBinding
import com.example.instatagg.presentation.viewmodel.MainViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class TaggsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTaggsBinding
    private val viewModel: MainViewModel by viewModel()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTaggsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnCreateTagg.setOnClickListener {
            val createTaggFragment = CreateTaggFragment()
            createTaggFragment.show(supportFragmentManager,"createTagg")
        }
    }
}