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
import androidx.viewpager2.widget.ViewPager2
import com.example.instatagg.R
import com.example.instatagg.databinding.ActivityFullscreanPhotoBinding
import com.example.instatagg.domain.model.Photo
import com.example.instatagg.domain.model.Tagg
import com.example.instatagg.presentation.adapter.FullscreenPhotoAdapter
import com.example.instatagg.presentation.adapter.MainAdapter
import com.example.instatagg.presentation.viewmodel.FullscreanPhotoViewModel
import com.example.instatagg.utils.OnItemClickListener
import com.example.instatagg.utils.addOnItemClickListener
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File

class FullscreenPhotoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFullscreanPhotoBinding
    private val viewModel: FullscreanPhotoViewModel by viewModel()
    private lateinit var mainAdapter: MainAdapter
    private lateinit var pageAdapter: FullscreenPhotoAdapter
    private lateinit var viewPager: ViewPager2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFullscreanPhotoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val firstPhoto = intent.getParcelableExtra<Photo>("photo")!!
        val position = intent.getIntExtra("position", 0)
        pageAdapter = FullscreenPhotoAdapter(this, arrayListOf())
        viewPager = binding.vpFullscreenPhoto
        viewPager.adapter = pageAdapter
        viewPager.post { viewPager.currentItem = position }
        mainAdapter = MainAdapter(arrayListOf())
        binding.rvChooseTagg.adapter = mainAdapter
        binding.rvChooseTagg.layoutManager = LinearLayoutManager(this)
        viewModel.getPhotos(firstPhoto.tagg!!.id!!).observe(this, {
            refreshPagerAdapter(it)
        })
        binding.ibDelete.setOnClickListener {
            getPhotoFullscreen().id?.let { it -> viewModel.delPhoto(it) }
            applicationContext.deleteFile(getPhotoFullscreen().path!!
                .substring(getPhotoFullscreen().path!!.lastIndexOf("/")+1))
            val photosActivity = Intent(this, PhotosActivity::class.java)
            photosActivity.putExtra("tagg", getPhotoFullscreen().tagg)
            this.finish()
            this.overridePendingTransition(0,0)
            startActivity(photosActivity)
        }

        binding.ibShare.setOnClickListener {
            val contentUri = FileProvider.getUriForFile(
                applicationContext,
                "com.example.instatagg.fileprovider",
                File(Uri.parse(getPhotoFullscreen().path).path)
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
            refreshMainAdapter(it)
        })

    }

    private fun getPhotoFullscreen(): Photo {
        return pageAdapter.photos[viewPager.currentItem]
    }

    private fun refreshMainAdapter(taggs: List<Tagg>){
        mainAdapter.apply {
            addTaggs(taggs)
            notifyDataSetChanged()
        }
    }
    private fun refreshPagerAdapter(photos: List<Photo>){
        pageAdapter.apply {
            addPhotos(photos)
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
        val photo = getPhotoFullscreen()
        when (item.itemId) {
            R.id.move_to_tagg -> {
                binding.cvChooseTagg.isVisible = true
                binding.rvChooseTagg.addOnItemClickListener(object: OnItemClickListener {
                    override fun onItemClicked(position: Int, view: View) {
                        val tagg = mainAdapter.getTagg(position)
                        val photosActivity = Intent(this@FullscreenPhotoActivity, PhotosActivity::class.java)
                        val oldTagg = photo.tagg
                        moveToTagg(tagg)
                        photosActivity.putExtra("tagg", oldTagg)
                        startActivity(photosActivity)
                    }
                })
                return true
            }
            R.id.copy_to_tagg -> {
                binding.cvChooseTagg.isVisible = true
                binding.rvChooseTagg.addOnItemClickListener(object: OnItemClickListener {
                    override fun onItemClicked(position: Int, view: View) {
                        val oldTagg = photo.tagg
                        val tagg = mainAdapter.getTagg(position)
                        photo.tagg = tagg
                        val photosActivity = Intent(this@FullscreenPhotoActivity, PhotosActivity::class.java)
                        viewModel.insertPhoto(photo)
                        photosActivity.putExtra("tagg", oldTagg)
                        startActivity(photosActivity)
                    }
                })
                return true
            }
        }
        return super.onContextItemSelected(item)
    }
    fun moveToTagg(tagg: Tagg){
        val photo = getPhotoFullscreen()
        viewModel.movePhoto(tagg.name, tagg.color, tagg.id!!, photo.id!!)
    }
    override fun onBackPressed() {
        if(binding.cvChooseTagg.isVisible) {
            binding.cvChooseTagg.visibility = View.GONE
        } else {
            val photosActivity = Intent(this, PhotosActivity::class.java)
            val photo = getPhotoFullscreen()
            photosActivity.putExtra("tagg", photo.tagg)
            startActivity(photosActivity)
            overridePendingTransition(0, 0)
        }
    }
}