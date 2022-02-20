package com.jnsoft.instatagg.presentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.jnsoft.instatagg.databinding.ActivitySingleBinding

class SingleActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySingleBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySingleBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}