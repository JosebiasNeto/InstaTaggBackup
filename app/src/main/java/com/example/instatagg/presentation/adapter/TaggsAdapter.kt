package com.example.instatagg.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.instatagg.R
import com.example.instatagg.domain.model.Tagg
import com.squareup.picasso.Picasso

class TaggsAdapter(private val taggs: ArrayList<Tagg>):
    RecyclerView.Adapter<TaggsAdapter.TaggsHolder>(){

    class TaggsHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(tagg: Tagg){
            itemView.apply {
                findViewById<TextView>(R.id.tv_name_tagg).text = tagg.name
                findViewById<TextView>(R.id.tv_name_tagg).setTextColor(tagg.color)
                Picasso.get().load(R.drawable.tagg_empty)
                    .into(itemView.findViewById<ImageView>(R.id.iv_tagg))
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaggsHolder =
        TaggsHolder(
            LayoutInflater.from(parent.context)
            .inflate(R.layout.tagg_item, parent, false))

    override fun onBindViewHolder(holder: TaggsHolder, position: Int) =
        holder.bind(taggs[position])

    override fun getItemCount(): Int = taggs.size

    fun getTagg(position: Int) = taggs[position]

    fun addTaggs(taggs: List<Tagg>){
        this.taggs.addAll(taggs)
    }
}