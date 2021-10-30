package com.example.instatagg.presentation.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.instatagg.R
import com.example.instatagg.databinding.ActivityPhotosBinding
import com.example.instatagg.domain.model.Tagg
import com.example.instatagg.presentation.viewmodel.MainViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.koin.androidx.viewmodel.ext.android.viewModel

class PhotosActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPhotosBinding
    private val viewModel: MainViewModel by viewModel()
    private var isFABOpen: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPhotosBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val tagg = intent.getParcelableExtra<Tagg>("tagg")
        val fabTaggEdit = binding.fabTaggEdit
        val fabTaggDelete = binding.fabTaggDelete
        if(tagg != null) {
            fabTaggDelete.setOnClickListener { deleteTagg(tagg) }
            fabTaggEdit.setOnClickListener { editTagg(tagg) }
        }
        binding.fabTaggOptions.setOnClickListener {
            if(!isFABOpen){
                showFABMenu(fabTaggEdit, fabTaggDelete)
            } else closeFABMenu(fabTaggEdit, fabTaggDelete)
        }
    }
    fun showFABMenu(fabTaggEdit: FloatingActionButton, fabTaggDelete: FloatingActionButton){
        isFABOpen = true
        fabTaggEdit.animate().translationY(resources.getDimension(R.dimen.standard_55))
        fabTaggDelete.animate().translationY(resources.getDimension(R.dimen.standard_105))
    }
    fun closeFABMenu(fabTaggEdit: FloatingActionButton, fabTaggDelete: FloatingActionButton){
        isFABOpen = false
        fabTaggEdit.animate().translationY(0F)
        fabTaggDelete.animate().translationY(0F)
    }
    fun deleteTagg(tagg: Tagg){
        tagg.id?.let { viewModel.delTagg(it) }
        val TaggsActivity = Intent(this, TaggsActivity::class.java)
        startActivity(TaggsActivity)
    }
    fun editTagg(tagg: Tagg){
        val editTaggFragment = EditTaggFragment(tagg)
        editTaggFragment.show(supportFragmentManager,"editTagg")
    }

}