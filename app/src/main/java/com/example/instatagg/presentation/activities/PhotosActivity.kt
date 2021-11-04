package com.example.instatagg.presentation.activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.instatagg.R
import com.example.instatagg.databinding.ActivityPhotosBinding
import com.example.instatagg.domain.model.Photo
import com.example.instatagg.domain.model.Tagg
import com.example.instatagg.presentation.adapter.PhotosAdapter
import com.example.instatagg.presentation.fragments.EditTaggFragment
import com.example.instatagg.presentation.viewmodel.PhotosViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class PhotosActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPhotosBinding
    private val viewModel: PhotosViewModel by viewModel()
    private var isFABOpen: Boolean = false
    private lateinit var adapter: PhotosAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPhotosBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        val tagg = intent.getParcelableExtra<Tagg>("tagg")!!
        supportActionBar!!.setTitle(tagg.name)
        binding.toolbar.setBackgroundColor(tagg.color)

            adapter = PhotosAdapter(arrayListOf())
        binding.rvPhotos.adapter = adapter
        binding.rvPhotos.layoutManager = GridLayoutManager(this, 3)
        if (tagg != null) {
            tagg.id?.let {
                viewModel.getPhotos(it).observe(this, {
                    refreshAdapter(it)
                })
            }
        }
    }
    private fun refreshAdapter(photos: List<Photo>){
        adapter.apply {
            addPhotos(photos)
            notifyDataSetChanged()
        }
    }

    fun clearTagg(tagg: Tagg){
        tagg.id?.let { viewModel.clearTagg(it) }
        finish()
        startActivity(intent)
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

    override fun onBackPressed() {
        startActivity(Intent(this, TaggsActivity::class.java))
        overridePendingTransition(0,0)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.tagg_option_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val tagg = intent.getParcelableExtra<Tagg>("tagg")!!

        when (item.itemId) {
            R.id.tagg_edit -> {
                editTagg(tagg)
                return true
            }
            R.id.clear_tagg -> {
                clearTagg(tagg)
                return true
            }
            R.id.delete_tagg -> {
                deleteTagg(tagg)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}