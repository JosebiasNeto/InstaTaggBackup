package com.jnsoft.instatagg.presentation.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.jnsoft.instatagg.databinding.ActivityTaggsBinding
import com.jnsoft.instatagg.domain.model.Tagg
import com.jnsoft.instatagg.presentation.adapter.TaggsAdapter
import com.jnsoft.instatagg.presentation.fragments.CreateTaggFragment
import com.jnsoft.instatagg.presentation.viewmodel.TaggsViewModel
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
        adapter = TaggsAdapter(arrayListOf())
        binding.rvTaggs.adapter = adapter

        viewModel.getTaggs().observe(this,{
            refreshAdapter(it)
        })

        binding.rvTaggs.addOnItemClickListener(object : OnItemClickListener{
            override fun onItemClicked(position: Int, view: View) {
                openPhotosActivity(position)
            }
        })
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
        startActivity(Intent(this, MainActivity::class.java))
    }
}