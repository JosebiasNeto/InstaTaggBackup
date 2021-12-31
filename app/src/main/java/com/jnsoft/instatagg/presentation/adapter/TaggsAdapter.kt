package com.jnsoft.instatagg.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.jnsoft.instatagg.R
import com.jnsoft.instatagg.domain.model.Tagg

class TaggsAdapter(private val taggs: ArrayList<Tagg>) :
    RecyclerView.Adapter<TaggsAdapter.TaggsHolder>() {

    class TaggsHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val taggName = itemView.findViewById<TextView>(R.id.tv_name_tagg)
        private val cvTagg = itemView.findViewById<CardView>(R.id.cv_tagg)
        private val taggSize = itemView.findViewById<TextView>(R.id.tv_size_tagg)
        fun bind(tagg: Tagg) {
            itemView.apply {
                taggName.text = tagg.name
                cvTagg.setCardBackgroundColor(tagg.color)
                taggSize.text = tagg.size.toString()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaggsHolder =
        TaggsHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.tagg_item, parent, false)
        )

    override fun onBindViewHolder(holder: TaggsHolder, position: Int) =
        holder.bind(taggs[position])

    override fun getItemCount(): Int = taggs.size

    fun getTagg(position: Int) = taggs[position]

    fun addTaggs(taggs: List<Tagg>) {
        this.taggs.addAll(taggs)
    }
}