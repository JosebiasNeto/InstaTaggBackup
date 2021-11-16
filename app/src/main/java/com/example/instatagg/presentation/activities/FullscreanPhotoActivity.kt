package com.example.instatagg.presentation.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.ContextMenu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.instatagg.R
import com.example.instatagg.databinding.ActivityFullscreanPhotoBinding
import com.example.instatagg.domain.model.Photo
import com.example.instatagg.domain.model.Tagg
import com.example.instatagg.presentation.adapter.MainAdapter
import com.example.instatagg.presentation.viewmodel.FullscreanPhotoViewModel
import com.example.instatagg.utils.OnItemClickListener
import com.example.instatagg.utils.addOnItemClickListener
import com.squareup.picasso.Picasso
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File

class FullscreanPhotoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFullscreanPhotoBinding
    private val viewModel: FullscreanPhotoViewModel by viewModel()
    private lateinit var adapter: MainAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFullscreanPhotoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val photo = intent.getParcelableExtra<Photo>("photo")!!
        Picasso.get().load(photo.path).noFade().resize(1440,1920)
            .into(binding.ivFullscreanPhoto)

        adapter = MainAdapter(arrayListOf())
        binding.rvChooseTagg.adapter = adapter
        binding.rvChooseTagg.layoutManager = LinearLayoutManager(this)

        binding.ibDelete.setOnClickListener {
            photo.id?.let { it -> viewModel.delPhoto(it) }
            val photosActivity = Intent(this, PhotosActivity::class.java)
            photosActivity.putExtra("tagg", photo.tagg)
            this.finish()
            this.overridePendingTransition(0,0)
            startActivity(photosActivity)
        }

        binding.ibShare.setOnClickListener {
            val contentUri = FileProvider.getUriForFile(
                applicationContext,
                "com.example.instatagg.fileprovider",
                File(Uri.parse(photo.path).path)
            )
            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_STREAM, contentUri)
                flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                type = "image/jpg"
            }
            startActivity(Intent.createChooser(shareIntent, "shareImage"))
        }
        registerForContextMenu(binding.ibMore)
        binding.ibMore.setOnClickListener { openContextMenu(it) }

        viewModel.getTaggs().observe(this, {
            refreshAdapter(it)
        })
    }
    private fun refreshAdapter(taggs: List<Tagg>){
        adapter.apply {
            addTaggs(taggs)
            notifyDataSetChanged()
        }
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
                        val tagg = adapter.getTagg(position)
                        val photo = intent.getParcelableExtra<Photo>("photo")!!
                        val photosActivity = Intent(this@FullscreanPhotoActivity, PhotosActivity::class.java)
                        val oldTagg = photo.tagg
                        moveToTagg(tagg)
                        photosActivity.putExtra("tagg", oldTagg)
                        startActivity(photosActivity)
                    }
                })
                return true
            }
            R.id.copy_to_tagg -> {

                return true
            }
        }
        return super.onContextItemSelected(item)
    }
    fun moveToTagg(tagg: Tagg){
        val photo = intent.getParcelableExtra<Photo>("photo")!!
        viewModel.movePhoto(tagg.name, tagg.color, tagg.id!!, photo.id!!)
    }
    override fun onBackPressed() {
        val photosActivity = Intent(this, PhotosActivity::class.java)
        val photo = intent.getParcelableExtra<Photo>("photo")!!
        photosActivity.putExtra("tagg", photo.tagg)
        startActivity(photosActivity)
        overridePendingTransition(0,0)
    }
}