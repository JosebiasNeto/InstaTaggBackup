package com.jnsoft.instatagg.presentation.taggs

import android.content.Intent
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.jnsoft.instatagg.databinding.ActivityTaggsBinding
import com.jnsoft.instatagg.domain.model.Tagg
import com.jnsoft.instatagg.presentation.camera.CameraActivity
import com.jnsoft.instatagg.presentation.photos.PhotosActivity
import com.jnsoft.instatagg.utils.OnItemClickListener
import com.jnsoft.instatagg.utils.addOnItemClickListener
import org.koin.androidx.viewmodel.ext.android.viewModel

class TaggsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTaggsBinding
    private val viewModel: TaggsViewModel by viewModel()
    private lateinit var adapter: TaggsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTaggsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnCreateTagg.setOnClickListener {
            val createTaggFragment = CreateTaggFragment()
            createTaggFragment.show(supportFragmentManager,"createTagg")
        }
        binding.rvTaggs.layoutManager = GridLayoutManager(this,3)
        setTaggsAdapter()
        binding.rvTaggs.adapter = adapter

        viewModel.getTaggs().observe(this,{
            refreshAdapter(it.reversed())
            setTotalSize(it)
        })

        binding.rvTaggs.addOnItemClickListener(object : OnItemClickListener{
            override fun onItemClicked(position: Int, view: View) {
                openPhotosActivity(position)
            }
        })
    }

    private fun setTaggsAdapter() {
        val outMetrics = DisplayMetrics()
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            val display = this.display
            display?.getRealMetrics(outMetrics)
        } else {
            @Suppress("DEPRECATION")
            val display = this.windowManager.defaultDisplay
            @Suppress("DEPRECATION")
            display.getMetrics(outMetrics)
        }
        adapter = TaggsAdapter(arrayListOf(),outMetrics.widthPixels)
    }

    private fun setTotalSize(taggs: List<Tagg>) {
        val totalSize = arrayListOf<Int>()
        taggs.map { totalSize.add(it.size) }
        if(totalSize.sum().toString().length > 6){
            binding.tvTotalSize.text = totalSize.sum().toString().substring(0,
                totalSize.sum().toString().length - 6)
        } else binding.tvTotalSize.text = "0"
    }

    private fun refreshAdapter(taggs: List<Tagg>){
        adapter.apply {
            addTaggs(taggs)
            notifyDataSetChanged()
        }
    }

    private fun openPhotosActivity(idTagg: Int) {
        val photosActivity = Intent(this, PhotosActivity::class.java)
        val tagg = adapter.getTagg(idTagg)
        photosActivity.putExtra("tagg", tagg)
        startActivity(photosActivity)
        overridePendingTransition(0,0)
    }

    override fun onBackPressed() {
        startActivity(Intent(this, CameraActivity::class.java))
    }
}