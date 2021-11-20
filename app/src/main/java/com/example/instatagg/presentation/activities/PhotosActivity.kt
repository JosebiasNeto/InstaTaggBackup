package com.example.instatagg.presentation.activities

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.CheckBox
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import com.example.instatagg.R
import com.example.instatagg.databinding.ActivityPhotosBinding
import com.example.instatagg.domain.model.Photo
import com.example.instatagg.domain.model.Tagg
import com.example.instatagg.presentation.adapter.PhotosAdapter
import com.example.instatagg.presentation.fragments.EditTaggFragment
import com.example.instatagg.presentation.viewmodel.PhotosViewModel
import com.example.instatagg.utils.OnItemClickListener
import com.example.instatagg.utils.addOnItemClickListener
import org.koin.androidx.viewmodel.ext.android.viewModel

class PhotosActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPhotosBinding
    private val viewModel: PhotosViewModel by viewModel()
    private var isFABOpen: Boolean = false
    private lateinit var adapter: PhotosAdapter
    private lateinit var holder: PhotosAdapter.PhotosHolder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPhotosBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        val tagg = intent.getParcelableExtra<Tagg>("tagg")!!
        supportActionBar!!.setTitle(tagg.name)
        binding.toolbar.setBackgroundColor(tagg.color)
        adapter = PhotosAdapter(arrayListOf(), this)
        binding.rvPhotos.adapter = adapter
        binding.rvPhotos.layoutManager = GridLayoutManager(this, 3)
        if (tagg != null) {
            tagg.id?.let {
                viewModel.getPhotos(it).observe(this, {
                    refreshAdapter(it)
                })
            }
        }
        binding.rvPhotos.addOnItemClickListener(object : OnItemClickListener {
            override fun onItemClicked(position: Int, view: View) {
                if(view.findViewById<CheckBox>(R.id.checkBox).isVisible){
                    view.findViewById<CheckBox>(R.id.checkBox).isChecked =
                            !view.findViewById<CheckBox>(R.id.checkBox).isChecked
                } else {
                    openFullscrean(position)
                    this@PhotosActivity.finish()
                }
            }
        })
        binding.ibDelete.setOnClickListener {

        }

        binding.ibShare.setOnClickListener {

        }
        registerForContextMenu(binding.ibMore)
        binding.ibMore.setOnClickListener { openContextMenu(it) }
    }

    fun changeBottomOptionsVisibility(){
        binding.cvBottom.isVisible = !binding.cvBottom.isVisible
    }

    private fun openFullscrean(position: Int) {
        val fullscreanPhotoActivity = Intent(this, FullscreanPhotoActivity::class.java)
        val photo = adapter.getPhoto(position)
        fullscreanPhotoActivity.putExtra("photo", photo)
        startActivity(fullscreanPhotoActivity)
        overridePendingTransition(0,0)
    }

    private fun refreshAdapter(photos: List<Photo>){
        adapter.apply {
            addPhotos(photos)
            notifyDataSetChanged()
        }
    }

    private fun clearTagg(tagg: Tagg){
        tagg.id?.let { viewModel.clearTagg(it) }
        finish()
        startActivity(intent)
    }

    private fun deleteTagg(tagg: Tagg){
        tagg.id?.let { viewModel.delTagg(it) }
        val taggsActivity = Intent(this, TaggsActivity::class.java)
        startActivity(taggsActivity)
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
    override fun onCreateContextMenu(
            menu: ContextMenu?,
            v: View?,
            menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        super.onCreateContextMenu(menu, v, menuInfo)
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.photos_option_menu, menu)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        val photo = intent.getParcelableExtra<Photo>("photo")!!
        when (item.itemId) {
            R.id.move_to_tagg -> {
                binding.rvChooseTagg.isVisible = true
                binding.rvChooseTagg.addOnItemClickListener(object: OnItemClickListener {
                    override fun onItemClicked(position: Int, view: View) {

                    }
                })
                return true
            }
            R.id.copy_to_tagg -> {
                binding.rvChooseTagg.isVisible = true
                binding.rvChooseTagg.addOnItemClickListener(object: OnItemClickListener {
                    override fun onItemClicked(position: Int, view: View) {

                    }
                })
                return true
            }
        }
        return super.onContextItemSelected(item)
    }
    fun moveToTagg(tagg: Tagg){

    }
}