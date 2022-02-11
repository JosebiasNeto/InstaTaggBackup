package com.jnsoft.instatagg.presentation.camera

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.jnsoft.instatagg.R
import com.jnsoft.instatagg.domain.model.Tagg

class MiniTaggsAdapter(private val taggs: ArrayList<Tagg>):
    RecyclerView.Adapter<MiniTaggsAdapter.MainHolder>(){

    class MainHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(tagg: Tagg){
            itemView.apply {
                findViewById<TextView>(R.id.chose_tagg_text).text = tagg.name
                findViewById<CardView>(R.id.chose_tagg_color).setCardBackgroundColor(tagg.color)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainHolder =
        MainHolder(LayoutInflater.from(parent.context)
            .inflate(R.layout.tagg_item_rv, parent, false))

    override fun onBindViewHolder(holder: MainHolder, position: Int) =
        holder.bind(taggs[position])

    override fun getItemCount(): Int = taggs.size

    fun getTagg(position: Int) = taggs[position]

    fun addTaggs(taggs: List<Tagg>){
        this.taggs.apply {
            clear()
            addAll(taggs)
        }
    }
}