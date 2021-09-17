package com.example.instatagg.presentation.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.instatagg.databinding.ActivityTaggsBinding
import com.example.instatagg.domain.model.Tagg
import com.example.instatagg.presentation.adapter.TaggsAdapter
import com.example.instatagg.presentation.viewmodel.MainViewModel
import com.example.instatagg.utils.OnItemClickListener
import com.example.instatagg.utils.addOnItemClickListener
import org.koin.androidx.viewmodel.ext.android.viewModel

class TaggsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTaggsBinding
    private val viewModel: MainViewModel by viewModel()
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
                openPhotosActivity()
            }

        })

    }
    private fun refreshAdapter(taggs: List<Tagg>){
        adapter.apply {
            addTaggs(taggs)
            notifyDataSetChanged()
        }
    }

    private fun openPhotosActivity() {

    }
}