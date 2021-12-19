package com.jnsoft.instatagg.presentation.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.CheckBox
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.net.toFile
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.jnsoft.instatagg.R
import com.jnsoft.instatagg.databinding.ActivityPhotosBinding
import com.jnsoft.instatagg.domain.model.Photo
import com.jnsoft.instatagg.domain.model.Tagg
import com.jnsoft.instatagg.presentation.adapter.MainAdapter
import com.jnsoft.instatagg.presentation.adapter.PhotosAdapter
import com.jnsoft.instatagg.presentation.fragments.EditTaggFragment
import com.jnsoft.instatagg.presentation.viewmodel.PhotosViewModel
import com.jnsoft.instatagg.utils.OnItemClickListener
import com.jnsoft.instatagg.utils.addOnItemClickListener
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File

class PhotosActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPhotosBinding
    private val viewModel: PhotosViewModel by viewModel()
    private lateinit var adapter: PhotosAdapter
    private lateinit var adapterMain: MainAdapter
    private lateinit var tagg: Tagg

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPhotosBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        tagg = intent.getParcelableExtra<Tagg>("tagg")!!
        tagg.id?.let {
            viewModel.getTagg(it).observe(this,{
                supportActionBar!!.title = it.name
                binding.toolbar.setBackgroundColor(it.color)
                binding.tvTotalSize.text = it.size.toString()
            })
        }
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

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
                   adapter.getPhoto(position).checked =
                           !adapter.getPhoto(position).checked
                } else {
                    openFullscreen(position)
                    this@PhotosActivity.finish()
                }
            }
        })
        binding.ibDelete.setOnClickListener {
            for(i in 0 until adapter.itemCount){
                if(adapter.getPhoto(i).checked) {
                    adapter.getPhoto(i).let { it1 ->
                        viewModel.delPhoto(it1,(it1.path!!.toUri()
                            .toFile().length()/(1024*1024)).toInt())
                    }
                    applicationContext.deleteFile(adapter.getPhoto(i).path!!
                        .substring(adapter.getPhoto(i).path!!.lastIndexOf("/")+1))
                }
            }
            finish()
            overridePendingTransition(0,0)
            startActivity(intent)
            overridePendingTransition(0,0)
        }

        binding.ibShare.setOnClickListener {
            val contentUris = arrayListOf<Uri>()
            for(i in 0 until adapter.itemCount){
                var uri = FileProvider.getUriForFile(
                    applicationContext,
                    "com.jnsoft.instatagg.fileprovider",
                    File(Uri.parse(adapter.getPhoto(i).path).path))
                if(adapter.getPhoto(i).checked){
                    contentUris.add(uri)
                }
            }
            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND_MULTIPLE
                putParcelableArrayListExtra(Intent.EXTRA_STREAM, contentUris)
                flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                type = "image/jpg"
            }
            startActivity(Intent.createChooser(shareIntent, "shareImage"))
        }
        registerForContextMenu(binding.ibMore)
        binding.ibMore.setOnClickListener { openContextMenu(it) }

        binding.fabFromTaggToCamera.setOnClickListener {
            val mainActivity = Intent(this, MainActivity::class.java)
            mainActivity.putExtra("tagg", tagg)
            startActivity(mainActivity)
            overridePendingTransition(0,0)
        }
        adapterMain = MainAdapter(arrayListOf())
        binding.rvChooseTagg.adapter = adapterMain
        binding.rvChooseTagg.layoutManager = LinearLayoutManager(this)
        viewModel.getTaggs().observe(this, {
            refreshAdapterMain(it)
        })
    }

    fun changeBottomOptionsVisibility(){
        binding.cvBottom.isVisible = !binding.cvBottom.isVisible
        binding.fabFromTaggToCamera.isVisible = !binding.fabFromTaggToCamera.isVisible
        binding.cvTotalSize.isVisible = !binding.cvTotalSize.isVisible
    }

    private fun openFullscreen(position: Int) {
        val fullscreenPhotoActivity = Intent(this, FullscreenPhotoActivity::class.java)
        val photo = adapter.getPhoto(position)
        fullscreenPhotoActivity.putExtra("photo", photo)
        fullscreenPhotoActivity.putExtra("position", position)
        startActivity(fullscreenPhotoActivity)
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
        for(i in 0 until adapter.itemCount){
                adapter.getPhoto(i).let { it1 ->
                        viewModel.delPhoto(
                            it1,
                            (it1.path!!.toUri().toFile().length()/(1024*1024)).toInt())
                }
                applicationContext.deleteFile(adapter.getPhoto(i).path!!
                    .substring(adapter.getPhoto(i).path!!.lastIndexOf("/")+1))
        }
        val taggsActivity = Intent(this, TaggsActivity::class.java)
        startActivity(taggsActivity)
    }
    fun editTagg(tagg: Tagg){
        val editTaggFragment = EditTaggFragment(tagg)
        editTaggFragment.show(supportFragmentManager,"editTagg")
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.tagg_option_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val tagg = intent.getParcelableExtra<Tagg>("tagg")!!
        when (item.itemId) {
//            R.id.import_photos -> {
//
//                return true
//            }
            android.R.id.home -> {
                startActivity(Intent(this, TaggsActivity::class.java))
                return true
            }
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
    private fun refreshAdapterMain(taggs: List<Tagg>){
        adapterMain.apply {
            addTaggs(taggs)
            notifyDataSetChanged()
        }
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.move_to_tagg -> {
                binding.cvChooseTagg.isVisible = true
                binding.rvChooseTagg.addOnItemClickListener(object: OnItemClickListener {
                    override fun onItemClicked(position: Int, view: View) {
                        for(i in 0 until adapter.itemCount){
                            if(adapter.getPhoto(i).checked)
                                moveToTagg(adapterMain.getTagg(position), adapter.getPhoto(i))
                        }
                        finish()
                        startActivity(intent)
                    }
                })
                return true
            }
            R.id.copy_to_tagg -> {
                binding.cvChooseTagg.isVisible = true
                binding.rvChooseTagg.addOnItemClickListener(object: OnItemClickListener {
                    override fun onItemClicked(position: Int, view: View) {
                        for(i in 0 until adapter.itemCount){
                            if(adapter.getPhoto(i).checked) {
                                adapter.getPhoto(i).tagg = adapterMain.getTagg(position)
                                viewModel.insertPhoto(adapter.getPhoto(i), 0)
                            }
                        }
                        finish()
                        startActivity(intent)
                    }
                })
                return true
            }
        }
        return super.onContextItemSelected(item)
    }
    fun moveToTagg(tagg: Tagg, photo: Photo){
        viewModel.movePhoto(tagg.name, tagg.color, tagg.id!!, photo.id!!,
        (photo.path!!.toUri().toFile().length()/(1024*1024)).toInt(), this.tagg.id!!)
    }

    override fun onBackPressed() {
        if(binding.cvChooseTagg.isVisible){
            binding.cvChooseTagg.visibility = View.GONE
        } else if(binding.cvBottom.isVisible){
            startActivity(intent)
            overridePendingTransition(0,0)
        } else {
            startActivity(Intent(this, TaggsActivity::class.java))
            overridePendingTransition(0,0)
        }
    }
}